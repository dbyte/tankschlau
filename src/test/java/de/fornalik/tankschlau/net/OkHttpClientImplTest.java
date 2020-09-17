package de.fornalik.tankschlau.net;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class OkHttpClientImplTest {
  private static final String DEMO_API_KEY = "00000000-0000-0000-0000-000000000002";

  @Test
  void newCall_happy() throws IOException {
    // given
    Request request = new RequestImpl();
    Response actualResponse;

    request.setBaseUrl(new URL("https://creativecommons.tankerkoenig.de/json/list.php?"));
    request.setMethod("GET");
    request.addHeader("Accept", "application/json; charset=utf-8");

    request.addUrlParameter("lat", "52.52099975265203");
    request.addUrlParameter("lng", "13.43803882598877");
    request.addUrlParameter("rad", "5");
    request.addUrlParameter("sort", "dist");
    request.addUrlParameter("type", "all");
    request.addUrlParameter("apikey", DEMO_API_KEY);

    HttpClient sut = new OkHttpClientImpl();

    // when
    actualResponse = sut.newCall(request, ResponseImpl.create());

    // then
    assertNotNull(actualResponse);
    assertFalse(actualResponse.getErrorMessage().isPresent());
    assertTrue(actualResponse.getBodyString().isPresent());

    System.out.println(actualResponse.getBodyString());
  }
}