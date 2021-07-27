package com.sngular.product.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;

import com.sngular.product.DataHelper;
import com.sngular.product.model.Product;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class ProductServiceTest {

  @Autowired ProductService srv;

  @MockBean ProductLegacyService productLegacySrv;

  Map<String, Product> data;

  @BeforeEach
  void setUp() throws Exception {
    data = DataHelper.createDB();
  }

  void prepareMockProductLegacyService(final List<String> idsSimilarsReturned) {
    when(productLegacySrv.getSimilarIds(anyString())).thenReturn(Mono.just(idsSimilarsReturned));
    data.forEach((s, p) -> when(productLegacySrv.getProduct(s)).thenReturn(Mono.just(p)));
  }

  @Test
  void givenSimilarIds_withoutId_thenThrowException() {
    assertThrows(ConstraintViolationException.class, () -> srv.getSimilarIds(null));
  }

  @Test
  void givenSimilarIds_whenGetSimilarsId_thenReturnProducts() {

    final var idsSimilarsReturned = new ArrayList<>(data.keySet());
    prepareMockProductLegacyService(idsSimilarsReturned);

    final var result = srv.getSimilarIds("1");
    final var list = result.block();

    idsSimilarsReturned.forEach(s -> list.contains(data.get(s)));

    verify(productLegacySrv, times(1)).getSimilarIds(anyString());
    verify(productLegacySrv, times(idsSimilarsReturned.size())).getProduct(anyString());
  }

  @Test
  void givenSimilarIds_whenGetSimilarsId_thenThrowNotFound() {

    when(productLegacySrv.getSimilarIds(anyString()))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    assertThrows(HttpClientErrorException.class, () -> srv.getSimilarIds("1"));

    verify(productLegacySrv, times(1)).getSimilarIds(anyString());
    verify(productLegacySrv, times(0)).getProduct(anyString());
  }

  @Test
  void givenSimilarIds_whenGetSimilarsId_whenGetProduct_thenThrowNotFound() {

    final var idsSimilarsReturned = new ArrayList<>(data.keySet());
    idsSimilarsReturned.add(DataHelper.ID_PRODUCT_NO_EXIST);
    prepareMockProductLegacyService(idsSimilarsReturned);
    when(productLegacySrv.getProduct(DataHelper.ID_PRODUCT_NO_EXIST))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    final var result = srv.getSimilarIds("1");
    assertThrows(HttpClientErrorException.class, () -> result.block());

    verify(productLegacySrv, times(1)).getSimilarIds(anyString());
    verify(productLegacySrv, times(idsSimilarsReturned.size())).getProduct(anyString());
  }
}
