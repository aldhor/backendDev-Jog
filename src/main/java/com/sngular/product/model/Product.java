package com.sngular.product.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@lombok.Value
@lombok.Builder
@lombok.extern.jackson.Jacksonized
public class Product {

  @NotEmpty String id;

  @NotEmpty String name;

  @NotNull BigDecimal price;

  @NotNull Boolean availability;
}
