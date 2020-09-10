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

import static org.junit.jupiter.api.Assertions.*;

class PetrolStationsJsonAdapterTest {
  private static Gson gson;

  @BeforeAll
  static void beforeAll() {
    gson = new GsonBuilder()
        .registerTypeAdapter(PetrolStation.class, new PetrolStationsJsonAdapter())
        .create();
  }

  @AfterAll
  static void afterAll() {
    gson = null;
  }

  @Test
  void read_oneStationHappy() {
    // given
    PetrolStationFixture fixture = PetrolStationFixture.newInstance().create_Berlin();
    JsonObject jsonResponse = TankerkoenigResponseMock
        .newInstance(fixture)
        .createJsonResponse();

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        jsonResponse,
        (Type) PetrolStation.class);

    // then
    assertNotNull(actualPetrolStations);
    assertEquals(1, actualPetrolStations.size());
    fixture.assertEquals(actualPetrolStations.get(0));
  }

  @Test
  void read_oneStationThrowsOnMissingIdElement() {
    // given
    PetrolStationFixture fixture = PetrolStationFixture.newInstance().create_Berlin();
    JsonObject jsonResponse = TankerkoenigResponseMock
        .newInstance(fixture, "id")
        .createJsonResponse();

    // when then
    assertThrows(PetrolStationsJsonAdapter.MissingElementException.class,
                 () -> gson.fromJson(jsonResponse, (Type) PetrolStation.class));
  }
}