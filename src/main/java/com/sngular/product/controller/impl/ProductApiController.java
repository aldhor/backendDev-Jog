package com.sngular.product.controller.impl;

import java.util.List;

import org.springframework.web.bind.annotation.RestController;

import com.sngular.product.controller.ProductApi;
import com.sngular.product.model.Product;
import com.sngular.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ProductApiController implements ProductApi {

  private final ProductService srv;

  @Override
  public Mono<List<Product>> getProductSimilar(final String productId) {
    return srv.getSimilarIds(productId);
  }
}
