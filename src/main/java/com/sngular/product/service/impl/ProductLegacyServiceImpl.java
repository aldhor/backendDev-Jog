package com.sngular.product.service.impl;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.circuitbreaker.NoFallbackAvailableException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException.NotFound;

import com.sngular.core.resilience.Resilence4jDecorator;
import com.sngular.product.model.Product;
import com.sngular.product.service.ProductLegacyService;

import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductLegacyServiceImpl implements ProductLegacyService {

  private final Resilence4jDecorator resilenceDecorator;

  @Value("${app.clients.productLegacy.host}")
  private String host;

  @Value("${app.clients.productLegacy.endpoint.similarIds}")
  private String endpontSimilarIds;

  @Value("${app.clients.productLegacy.endpoint.getProduct}")
  private String endpontGetProduct;

  @Override
  public Mono<List<String>> getSimilarIds(final String productId) {

    final var methodSign = "getSimilarIds" + productId;
    final var decorators =
        Map.of(
            Resilence4jDecorator.Type.BULK_HEAD,
            methodSign,
            Resilence4jDecorator.Type.CIRCUIT_BREAK,
            methodSign);

    return resilenceDecorator.decorate(
        WebClient.create()
            .get()
            .uri(host + endpontSimilarIds, productId)
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<List<String>>() {}),
        decorators,
        null);
  }

  @Override
  public Mono<Product> getProduct(final String productId) {

    final var methodSign = "getProduct" + productId;
    final var decorators =
        Map.of(
            Resilence4jDecorator.Type.BULK_HEAD,
            methodSign,
            Resilence4jDecorator.Type.TIME_LIMITER,
            methodSign,
            Resilence4jDecorator.Type.CIRCUIT_BREAK,
            methodSign);

    return resilenceDecorator.decorate(
        WebClient.create()
            .get()
            .uri(host + endpontGetProduct, productId)
            .retrieve()
            .bodyToMono(Product.class),
        decorators,
        new FallbackGetProduct());
  }

  static class FallbackGetProduct implements Function<Throwable, Mono<Product>> {

    @Override
    public Mono<Product> apply(final Throwable t) {
      if (t instanceof NotFound) {
        return Mono.empty();
      }
      if (t instanceof CallNotPermittedException) {
        throw (CallNotPermittedException) t;
      }
      throw new NoFallbackAvailableException("Sin Fallback", t);
    }
  }
}
