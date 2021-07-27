package com.sngular.product.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sngular.product.model.Product;
import com.sngular.product.service.ProductLegacyService;
import com.sngular.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductLegacyService productLegacySrv;

  @Override
  public Mono<List<Product>> getSimilarIds(final String productId) {
    final var similarIds = productLegacySrv.getSimilarIds(productId);
    return similarIds
        .flatMapMany(Flux::fromIterable)
        .flatMapSequential(productLegacySrv::getProduct)
        .collectList();
  }
}
