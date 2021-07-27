package com.sngular.product.service;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import com.sngular.product.model.Product;

import reactor.core.publisher.Mono;

@Validated
public interface ProductService {

  Mono<List<Product>> getSimilarIds(@NotNull String productId);
}
