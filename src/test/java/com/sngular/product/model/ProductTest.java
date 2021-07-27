package com.sngular.product.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductTest {

  private Validator validator;

  @BeforeEach
  public void init() {
    final var vf = Validation.buildDefaultValidatorFactory();
    this.validator = vf.getValidator();
  }

  @Test
  void validate_allOk() {
    final var dto =
        Product.builder().id(" ").name(" ").price(BigDecimal.ZERO).availability(true).build();
    final Set<ConstraintViolation<Product>> violations = validator.validate(dto);
    assertTrue(violations.isEmpty());
  }

  @Test
  void validate_allNull_fail() {
    final var dto = Product.builder().build();
    final Set<ConstraintViolation<Product>> violations = validator.validate(dto);
    assertEquals(4, violations.size());
  }

  @Test
  void validate_allNullOrEmpty_fail() {
    final var dto = Product.builder().id("").name("").build();
    final Set<ConstraintViolation<Product>> violations = validator.validate(dto);
    assertEquals(4, violations.size());
  }
}
