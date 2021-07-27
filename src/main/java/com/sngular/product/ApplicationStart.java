package com.sngular.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import com.sngular.core.CoreConfig;

@SpringBootApplication
@Import(CoreConfig.class)
public class ApplicationStart {

  public static void main(final String[] args) {
    SpringApplication.run(ApplicationStart.class, args);
  }
}
