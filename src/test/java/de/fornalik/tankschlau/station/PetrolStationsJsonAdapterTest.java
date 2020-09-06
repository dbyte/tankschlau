package de.fornalik.tankschlau.station;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.fornalik.tankschlau.geo.GeoLocation;
import de.fornalik.tankschlau.helpers.response.fixture.TankerkoenigResponseFixture;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PetrolStationsJsonAdapterTest {
  private static Gson gson;
  private static TankerkoenigResponseFixture responseFixture;

  @BeforeAll
  static void beforeAll() {
    responseFixture = new TankerkoenigResponseFixture();

    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(PetrolStation.class, new PetrolStationsJsonAdapter());
    gson = builder.create();
  }

  @AfterAll
  static void afterAll() {
    gson = null;
    responseFixture = null;
  }

  @Test
  void read_oneStationHappy()
      throws PetrolStation.PriceException {
    // given
    ArrayList<PetrolStation> actualPetrolStations;
    PetrolStation expectedPetrolStation = PetrolStationFixture.create_Berlin();
    JsonObject jsonResponse = responseFixture.createJson(expectedPetrolStation);

    // when
    actualPetrolStations = gson.fromJson(jsonResponse, (Type) PetrolStation.class);

    // then
    assertNotNull(actualPetrolStations);
    assertEquals(1, actualPetrolStations.size());

    PetrolStation actualPetrolStation = actualPetrolStations.get(0);
    assertEquals(expectedPetrolStation, actualPetrolStation);
  }
}