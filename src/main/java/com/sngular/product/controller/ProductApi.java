package com.sngular.product.controller;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.sngular.product.model.Product;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import reactor.core.publisher.Mono;

@Validated
@Api(value = "SimilarProducts", produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(
    value = "/product",
    produces = {MediaType.APPLICATION_JSON_VALUE})
public interface ProductApi {

  @ApiOperation(value = "List of similar products to a given one ordered by similarity")
  @ApiResponses({
    @ApiResponse(code = 200, message = "Ok"),
    @ApiResponse(
        code = 503,
        message =
            "Service Temporarily Unavailable. See the 'Retry-After' header with the recommended number of seconds to try again")
  })
  @GetMapping(value = "/{productId}/similar")
  Mono<List<Product>> getProductSimilar(@PathVariable("productId") @NotNull String productId);
}
