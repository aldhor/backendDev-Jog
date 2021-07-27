package com.sngular.core.controller;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.validation.ConstraintViolationException;

import org.springframework.boot.autoconfigure.web.WebProperties.Resources;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import reactor.core.publisher.Mono;

@Component
@Order(-10)
public class CustomControllerErrorHandler extends AbstractErrorWebExceptionHandler {

  public CustomControllerErrorHandler(
      final ErrorAttributes errorAttributes,
      final Resources resources,
      final ApplicationContext applicationContext,
      final ServerCodecConfigurer serverCodecConfigurer) {
    super(errorAttributes, resources, applicationContext);
    this.setMessageWriters(serverCodecConfigurer.getWriters());
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(
      final ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  protected Mono<ServerResponse> handleWebClientResponseException(
      final WebClientResponseException ex, final Map<String, Object> errorPropertiesMap) {
    final var status = HttpStatus.resolve(ex.getRawStatusCode());
    final var headers = new HttpHeaders();
    return handleExceptionInternal(ex, null, headers, status, errorPropertiesMap);
  }

  protected Mono<ServerResponse> handleResponseStatusException(
      final ResponseStatusException ex, final Map<String, Object> errorPropertiesMap) {
    final var status = ex.getStatus();
    final var headers = new HttpHeaders();
    return handleExceptionInternal(ex, null, headers, status, errorPropertiesMap);
  }

  protected Mono<ServerResponse> handleHttpClientErrorException(
      final HttpClientErrorException ex, final Map<String, Object> errorPropertiesMap) {
    final var status = ex.getStatusCode();
    final var headers = new HttpHeaders();
    return handleExceptionInternal(ex, null, headers, status, errorPropertiesMap);
  }

  protected Mono<ServerResponse> handleCallNotPermittedException(
      final CallNotPermittedException ex, final Map<String, Object> errorPropertiesMap) {
    final var status = HttpStatus.SERVICE_UNAVAILABLE;
    final var headers = new HttpHeaders();
    headers.add(HttpHeaders.RETRY_AFTER, "20");
    return handleExceptionInternal(ex, null, headers, status, errorPropertiesMap);
  }

  protected Mono<ServerResponse> handleConstraintViolationException(
      final ConstraintViolationException ex, final Map<String, Object> errorPropertiesMap) {
    final var status = HttpStatus.BAD_REQUEST;
    final var headers = new HttpHeaders();

    final var violations = ex.getConstraintViolations();
    final Set<String> messages = new HashSet<>(violations.size());
    messages.addAll(
        violations
            .stream()
            .map(
                v ->
                    String.format(
                        "{%s} value '%s' %s",
                        StreamSupport.stream(v.getPropertyPath().spliterator(), false)
                            .reduce((first, second) -> second)
                            .orElse(null),
                        v.getInvalidValue(),
                        v.getMessage()))
            .collect(Collectors.toList()));

    return handleExceptionInternal(ex, messages, headers, status, errorPropertiesMap);
  }

  protected Mono<ServerResponse> handleThrowable(
      final Throwable ex, final Map<String, Object> errorPropertiesMap) {
    final var status = HttpStatus.INTERNAL_SERVER_ERROR;
    final var headers = new HttpHeaders();
    return handleExceptionInternal(ex, null, headers, status, errorPropertiesMap);
  }

  protected Mono<ServerResponse> handleExceptionInternal(
      final Throwable ex,
      final Object body,
      final HttpHeaders headers,
      final HttpStatus status,
      final Map<String, Object> errorPropertiesMap) {
    final var myStatus = status == null ? HttpStatus.INTERNAL_SERVER_ERROR : status;
    final var r = ServerResponse.status(myStatus).contentType(MediaType.APPLICATION_JSON);
    headers.toSingleValueMap().forEach(r::header);
    return r.bodyValue(body == null ? myStatus.getReasonPhrase() : body);
  }

  private Mono<ServerResponse> renderErrorResponse(final ServerRequest request) {

    final var errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults());

    final var cause = getError(request);
    if (cause instanceof WebClientResponseException) {
      return handleWebClientResponseException(
          (WebClientResponseException) cause, errorPropertiesMap);
    }
    if (cause instanceof ResponseStatusException) {
      return handleResponseStatusException((ResponseStatusException) cause, errorPropertiesMap);
    }
    if (cause instanceof HttpClientErrorException) {
      return handleHttpClientErrorException((HttpClientErrorException) cause, errorPropertiesMap);
    }
    if (cause instanceof CallNotPermittedException) {
      return handleCallNotPermittedException((CallNotPermittedException) cause, errorPropertiesMap);
    }
    if (cause instanceof ConstraintViolationException) {
      return handleConstraintViolationException(
          (ConstraintViolationException) cause, errorPropertiesMap);
    }

    return handleThrowable(cause, errorPropertiesMap);
  }
}
