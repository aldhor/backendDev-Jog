package com.sngular.product.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sngular.product.model.Product;
import com.sngular.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@Validated
@RestController
@RequestMapping(
    value = "/product",
    produces = {"application/json"})
@RequiredArgsConstructor
public class ProductController {

  private final ProductService srv;

  @GetMapping(value = "/{productId}/similar")
  public Mono<List<Product>> getProductSimilar(
      @PathVariable("productId") @NotNull final String productId) {
    return srv.getSimilarIds(productId);
  }
}
