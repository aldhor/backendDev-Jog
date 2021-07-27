package com.sngular.product;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.sngular.product.model.Product;

import lombok.experimental.UtilityClass;

@UtilityClass
public class DataHelper {

  public final String ID_PRODUCT_NO_EXIST = "999";

  public Map<String, Product> createDB() {
    final Map<String, Product> db = new HashMap<>();
    db.put(
        "2",
        Product.builder()
            .id("2")
            .name("Dress")
            .price(new BigDecimal("19.99"))
            .availability(true)
            .build());
    db.put(
        "3",
        Product.builder()
            .id("3")
            .name("Blazer")
            .price(new BigDecimal("29.99"))
            .availability(false)
            .build());
    db.put(
        "4",
        Product.builder()
            .id("4")
            .name("Boots")
            .price(new BigDecimal("39.99"))
            .availability(true)
            .build());
    return db;
  }
}
