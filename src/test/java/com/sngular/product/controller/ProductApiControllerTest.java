package com.sngular.product.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.client.HttpClientErrorException;

import com.sngular.core.resilience.Resilence4jDecorator;
import com.sngular.product.DataHelper;
import com.sngular.product.controller.impl.ProductApiController;
import com.sngular.product.model.Product;
import com.sngular.product.service.ProductLegacyService;
import com.sngular.product.service.impl.ProductServiceImpl;

import reactor.core.publisher.Mono;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = ProductApiController.class)
@Import({ProductServiceImpl.class})
class ProductApiControllerTest {

  static final String URI = "/product/1/similar";

  @MockBean ProductLegacyService productLegacySrv;
  @MockBean Resilence4jDecorator resilence4jDecorator;
  @Autowired WebTestClient webClient;

  Map<String, Product> data;

  @BeforeEach
  void loadData() {
    data = DataHelper.createDB();
  }

  void prepareMockProductLegacyService(final List<String> idsSimilarsReturned) {
    when(productLegacySrv.getSimilarIds(anyString())).thenReturn(Mono.just(idsSimilarsReturned));
    data.forEach((s, p) -> when(productLegacySrv.getProduct(s)).thenReturn(Mono.just(p)));
    when(productLegacySrv.getProduct(DataHelper.ID_PRODUCT_NO_EXIST))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
  }

  @Test
  void givenSimilarIds_whenGetSimilarsId_thenReturnProducts() {

    final var idsSimilarsReturned = new ArrayList<>(data.keySet());
    prepareMockProductLegacyService(idsSimilarsReturned);

    final var spec =
        webClient.get().uri(URI).exchange().expectStatus().isOk().expectBodyList(Product.class);
    idsSimilarsReturned.forEach(s -> spec.contains(data.get(s)));

    verify(productLegacySrv, times(1)).getSimilarIds(anyString());
    verify(productLegacySrv, times(idsSimilarsReturned.size())).getProduct(anyString());
  }

  @Test
  void givenSimilarIds_whenGetSimilarsId_thenThrowNotFound() {

    when(productLegacySrv.getSimilarIds(anyString()))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    webClient.get().uri(URI).exchange().expectStatus().isNotFound();

    verify(productLegacySrv, times(1)).getSimilarIds(anyString());
    verify(productLegacySrv, times(0)).getProduct(anyString());
  }

  @Test
  void givenSimilarIds_whenGetSimilarsId_whenGetProduct_thenThrowNotFound() {

    final var idsSimilarsReturned = new ArrayList<>(data.keySet());
    prepareMockProductLegacyService(idsSimilarsReturned);
    idsSimilarsReturned.add(DataHelper.ID_PRODUCT_NO_EXIST);

    webClient.get().uri(URI).exchange().expectStatus().isNotFound();

    verify(productLegacySrv, times(1)).getSimilarIds(anyString());
    verify(productLegacySrv, times(idsSimilarsReturned.size())).getProduct(anyString());
  }
}
