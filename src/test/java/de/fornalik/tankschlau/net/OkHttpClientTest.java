package de.fornalik.tankschlau.net;

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStations;
import de.fornalik.tankschlau.station.PetrolStationsJsonAdapter;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OkHttpClientTest {

  @Test
  void newCall_happy() throws IOException {
    // given
    Geo userLocation = new Geo(52.52099975265203, 13.43803882598877, 5.0);
    PetrolStationsJsonAdapter gsonAdapter = new PetrolStationsJsonAdapter();
    Request request = TankerkoenigRequest.create(userLocation);

    HttpClient okhttpClient = new OkHttpClient();

    // when
    Response actualResponse = okhttpClient.newCall(request);

    // then
    assertNotNull(actualResponse);
    assertFalse(actualResponse.getErrorMessage().isPresent());
    assertTrue(actualResponse.getBodyString().isPresent());

    List<PetrolStation> petrolStations = PetrolStations.createFromJson(
        actualResponse.getBodyString().get(), gsonAdapter);

    System.out.println(petrolStations);
  }
}