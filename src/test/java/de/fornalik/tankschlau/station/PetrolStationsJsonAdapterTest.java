package de.fornalik.tankschlau.station;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.fornalik.tankschlau.helpers.response.FixtureFiles;
import de.fornalik.tankschlau.helpers.response.JsonResponseFixture;
import org.apache.commons.lang3.tuple.Pair;
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
    Pair<JsonResponseFixture, JsonObject> fixtures = JsonResponseFixture.createFromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    JsonResponseFixture objectFixture = fixtures.getLeft();
    JsonObject jsonFixture = fixtures.getRight();

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertNotNull(actualPetrolStations);
    assertEquals(1, actualPetrolStations.size());
    objectFixture.assertEquals(actualPetrolStations);
  }

  @Test
  void read_oneStationThrowsOnMissingIdElement() {
    // given
    JsonObject jsonFixture = JsonResponseFixture.createFromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_MISSING_ID_ELEM).getRight();

    // when then
    assertThrows(PetrolStationsJsonAdapter.MissingElementException.class,
                 () -> gson.fromJson(jsonFixture, (Type) PetrolStation.class));
  }

  @Test
  void read_oneStationThrowsOnMissingStationsElement() {
    // given
    JsonObject jsonFixture = JsonResponseFixture
        .createFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_STATIONS_ELEM)
        .getRight();

    // when then
    assertThrows(PetrolStationsJsonAdapter.MissingElementException.class,
                 () -> gson.fromJson(jsonFixture, (Type) PetrolStation.class));
  }

  @Test
  void read_ReturnsEmptyArrayOnEmptyStationsJsonArray() {
    // given
    Pair<JsonResponseFixture, JsonObject> fixtures = JsonResponseFixture.createFromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_STATION_ARRAY);

    JsonResponseFixture objectFixture = fixtures.getLeft();
    JsonObject jsonFixture = fixtures.getRight();

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertNotNull(actualPetrolStations);
    assertEquals(0, actualPetrolStations.size());
  }

  @Test
  void read_acceptsEmptyHouseNumberOrBrand() {
    // given
    JsonObject jsonFixture = JsonResponseFixture.createFromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_HOUSENUM_AND_BRAND).getRight();

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertNotNull(actualPetrolStations);

    assertEquals("",
                 actualPetrolStations.get(0).address.getHouseNumber());

    assertEquals("",
                 actualPetrolStations.get(0).brand);
  }
}