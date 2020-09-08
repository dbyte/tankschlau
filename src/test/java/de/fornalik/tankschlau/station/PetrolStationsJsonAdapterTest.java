package de.fornalik.tankschlau.station;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.fornalik.tankschlau.helpers.response.TankerkoenigResponseMock;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class PetrolStationsJsonAdapterTest {
  private static Gson gson;
  private static TankerkoenigResponseMock responseMock;

  @BeforeAll
  static void beforeAll() {
    responseMock = new TankerkoenigResponseMock();

    GsonBuilder builder = new GsonBuilder();
    builder.registerTypeAdapter(PetrolStation.class, new PetrolStationsJsonAdapter());
    gson = builder.create();
  }

  @AfterAll
  static void afterAll() {
    gson = null;
    responseMock = null;
  }

  @Test
  void read_oneStationHappy() {
    // given
    ArrayList<PetrolStation> actualPetrolStations;
    PetrolStation expectedPetrolStation = PetrolStationFixture.create_Berlin();
    JsonObject jsonResponse = responseMock.createJson(expectedPetrolStation);

    // when
    actualPetrolStations = gson.fromJson(jsonResponse, (Type) PetrolStation.class);

    // then
    assertNotNull(actualPetrolStations);
    assertEquals(1, actualPetrolStations.size());

    PetrolStation actualPetrolStation = actualPetrolStations.get(0);
    assertEquals(expectedPetrolStation, actualPetrolStation);
  }
}