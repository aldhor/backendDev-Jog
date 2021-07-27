package com.sngular.core.resilience;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadRegistry;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.reactor.bulkhead.operator.BulkheadOperator;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import io.github.resilience4j.reactor.ratelimiter.operator.RateLimiterOperator;
import io.github.resilience4j.reactor.retry.RetryOperator;
import io.github.resilience4j.reactor.timelimiter.TimeLimiterOperator;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import io.github.resilience4j.timelimiter.TimeLimiter;
import io.github.resilience4j.timelimiter.TimeLimiterRegistry;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class Resilence4jDecorator {

  public enum Type {
    BULK_HEAD,
    TIME_LIMITER,
    RATE_LIMITER,
    CIRCUIT_BREAK,
    RETRY;
  }

  private final BulkheadRegistry bulkheadRegistry;
  private final TimeLimiterRegistry timeLimiterRegistry;
  private final RateLimiterRegistry rateLimiterRegistry;
  private final CircuitBreakerRegistry circuitBreakerRegistry;
  private final RetryRegistry retryRegistry;

  private Bulkhead bulkhead(final String name) {
    return bulkheadRegistry.bulkhead(name);
  }

  private TimeLimiter timeLimiter(final String name) {
    return timeLimiterRegistry.timeLimiter(name);
  }

  private RateLimiter rateLimiter(final String name) {
    return rateLimiterRegistry.rateLimiter(name);
  }

  private CircuitBreaker circuitBreaker(final String name) {
    return circuitBreakerRegistry.circuitBreaker(name);
  }

  private Retry retry(final String name) {
    return retryRegistry.retry(name);
  }

  public <T> Mono<T> decorate(
      final Mono<T> mono,
      final String name,
      final List<Type> types,
      @Nullable final Function<Throwable, Mono<T>> fallback) {
    return decorate(mono, toMap(name, types), fallback);
  }

  public <T> Mono<T> decorate(
      final Mono<T> mono,
      final Map<Type, String> types,
      @Nullable final Function<Throwable, Mono<T>> fallback) {
    var r = mono;
    if (!CollectionUtils.isEmpty(types)) {
      if (types.containsKey(Type.BULK_HEAD)) {
        r = r.transformDeferred(BulkheadOperator.of(bulkhead(types.get(Type.BULK_HEAD))));
      }
      if (types.containsKey(Type.TIME_LIMITER)) {
        r = r.transformDeferred(TimeLimiterOperator.of(timeLimiter(types.get(Type.TIME_LIMITER))));
      }
      if (types.containsKey(Type.RATE_LIMITER)) {
        r = r.transformDeferred(RateLimiterOperator.of(rateLimiter(types.get(Type.RATE_LIMITER))));
      }
      if (types.containsKey(Type.CIRCUIT_BREAK)) {
        r =
            r.transformDeferred(
                CircuitBreakerOperator.of(circuitBreaker(types.get(Type.CIRCUIT_BREAK))));
      }
      if (types.containsKey(Type.RETRY)) {
        r = r.transformDeferred(RetryOperator.of(retry(types.get(Type.RETRY))));
      }
    }
    if (fallback != null) {
      r = r.onErrorResume(fallback);
    }
    return r;
  }

  public <T> Flux<T> decorate(
      final Flux<T> flux,
      final String name,
      final List<Type> types,
      @Nullable final Function<Throwable, Flux<T>> fallback) {
    return decorate(flux, toMap(name, types), fallback);
  }

  public <T> Flux<T> decorate(
      final Flux<T> flux,
      final Map<Type, String> types,
      @Nullable final Function<Throwable, Flux<T>> fallback) {
    var r = flux;
    if (!CollectionUtils.isEmpty(types)) {
      if (types.containsKey(Type.BULK_HEAD)) {
        r = r.transformDeferred(BulkheadOperator.of(bulkhead(types.get(Type.BULK_HEAD))));
      }
      if (types.containsKey(Type.TIME_LIMITER)) {
        r = r.transformDeferred(TimeLimiterOperator.of(timeLimiter(types.get(Type.TIME_LIMITER))));
      }
      if (types.containsKey(Type.RATE_LIMITER)) {
        r = r.transformDeferred(RateLimiterOperator.of(rateLimiter(types.get(Type.RATE_LIMITER))));
      }
      if (types.containsKey(Type.CIRCUIT_BREAK)) {
        r =
            r.transformDeferred(
                CircuitBreakerOperator.of(circuitBreaker(types.get(Type.CIRCUIT_BREAK))));
      }
      if (types.containsKey(Type.RETRY)) {
        r = r.transformDeferred(RetryOperator.of(retry(types.get(Type.RETRY))));
      }
    }
    if (fallback != null) {
      r = r.onErrorResume(fallback);
    }
    return r;
  }

  private static Map<Type, String> toMap(final String name, final List<Type> types) {
    Assert.notNull(name, "name required");
    return CollectionUtils.isEmpty(types)
        ? null
        : types.stream().collect(Collectors.toMap(m -> m, m -> name));
  }
}
