package com.sngular.product.model;

import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@lombok.Value
@lombok.Builder
@lombok.extern.jackson.Jacksonized
@ApiModel(description = "Information about Product")
public class Product {

  @ApiModelProperty(value = "Unique identifier", required = true, allowEmptyValue = false)
  @NotEmpty
  String id;

  @ApiModelProperty(value = "Name", required = true, allowEmptyValue = false)
  @NotEmpty
  String name;

  @ApiModelProperty(value = "Price", required = true)
  @NotNull
  BigDecimal price;

  @ApiModelProperty(value = "availability", required = true)
  @NotNull
  Boolean availability;
}
