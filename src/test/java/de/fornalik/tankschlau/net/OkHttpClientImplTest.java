package de.fornalik.tankschlau.net;

import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStations;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OkHttpClientImplTest {
  private static final String DEMO_API_KEY = "00000000-0000-0000-0000-000000000002";

  @Test
  void newCall_happy() throws IOException {
    // given
    Request request = PetrolStationNeighbourhoodRequest.create(
        52.52099975265203,
        13.43803882598877,
        5,
        DEMO_API_KEY);

    HttpClient okhttpClient = new OkHttpClientImpl();

    // when
    Response actualResponse = okhttpClient.newCall(request);

    // then
    assertNotNull(actualResponse);
    assertFalse(actualResponse.getErrorMessage().isPresent());
    assertTrue(actualResponse.getBodyString().isPresent());

    List<PetrolStation> petrolStations = PetrolStations.createFromJsonString(
        actualResponse.getBodyString().get());

    System.out.println(petrolStations);
  }
}