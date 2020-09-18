package de.fornalik.tankschlau.net;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class OkHttpClientImplTest {

  private static final String DEMO_API_KEY = "00000000-0000-0000-0000-000000000002";

  @Test
  void newCall_happy() throws IOException {
    // given
    Response actualResponse;
    Request request = PetrolStationNeighbourhoodRequest.create(
        52.52099975265203,
        13.43803882598877,
        5,
        DEMO_API_KEY);

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