package com.sngular.product.service;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.sngular.product.model.Product;

import reactor.core.publisher.Mono;

@Validated
public interface ProductLegacyService {

  Mono<List<String>> getSimilarIds(@NotNull String productId);

  Mono<Product> getProduct(@NotNull String productId);
}
