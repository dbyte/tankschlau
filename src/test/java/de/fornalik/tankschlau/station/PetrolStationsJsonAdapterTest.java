package de.fornalik.tankschlau.station;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.fornalik.tankschlau.helpers.response.FixtureFiles;
import de.fornalik.tankschlau.helpers.response.JsonResponseFixture;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.*;

import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PetrolStationsJsonAdapterTest {
  private Gson gson;
  private PetrolStationsJsonAdapter petrolStationsJsonAdapter;

  @BeforeEach
  void beforeEach() {
    petrolStationsJsonAdapter = new PetrolStationsJsonAdapter();
    gson = new GsonBuilder()
        .registerTypeAdapter(PetrolStation.class, petrolStationsJsonAdapter)
        .create();
  }

  @AfterEach
  void afterEach() {
    gson = null;
    petrolStationsJsonAdapter = null;
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
  void read_oneStationReturnsEmptyArrayOnMissingIdElement() {
    // given
    JsonObject jsonFixture = JsonResponseFixture.createFromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_MISSING_ID_ELEM).getRight();

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertEquals(0, actualPetrolStations.size());
    assertEquals(1, petrolStationsJsonAdapter.getErrorMessages().size());
  }

  @Test
  void read_returnsEmptyArrayOnMissingStationsElement() {
    // given
    JsonObject jsonFixture = JsonResponseFixture
        .createFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_STATIONS_ELEM)
        .getRight();

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertEquals(0, actualPetrolStations.size());
    assertEquals(1, petrolStationsJsonAdapter.getErrorMessages().size());
  }

  @Test
  void read_returnsEmptyArrayOnEmptyStationsJsonArray() {
    // given
    JsonObject jsonFixture = JsonResponseFixture.createFromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_STATION_ARRAY).getRight();

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertEquals(0, actualPetrolStations.size());
    assertEquals(1, petrolStationsJsonAdapter.getErrorMessages().size());
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