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
  private static Gson gson;
  private static PetrolStationsJsonAdapter petrolStationsJsonAdapter;

  @BeforeAll
  static void beforeAll() {
    petrolStationsJsonAdapter = new PetrolStationsJsonAdapter();
    gson = new GsonBuilder()
        .registerTypeAdapter(PetrolStation.class, petrolStationsJsonAdapter)
        .create();
  }

  @AfterAll
  static void afterAll() {
    gson = null;
    petrolStationsJsonAdapter = null;
  }

  @Test
  void read_oneStation_happy() {
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
    assertEquals(0, petrolStationsJsonAdapter.getErrorMessages().size());
    objectFixture.assertEquals(actualPetrolStations);
  }

  @Test
  void read_oneStation_returnsEmptyArrayOnMissingIdElement() {
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
  void read_noStations_returnsEmptyArrayOnMissingStationsElement() {
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
  void read_emptyStations_returnsEmptyArrayOnEmptyStationsJsonArray() {
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
  void read_multi_17HappyStations() {
    // given
    Pair<JsonResponseFixture, JsonObject> fixtures = JsonResponseFixture.createFromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_17HappyStations);

    JsonResponseFixture objectFixture = fixtures.getLeft();
    JsonObject jsonFixture = fixtures.getRight();

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertNotNull(actualPetrolStations);
    assertEquals(17, actualPetrolStations.size());
    assertEquals(0, petrolStationsJsonAdapter.getErrorMessages().size());
    objectFixture.assertEquals(actualPetrolStations);
  }

  @Test
  void read_multi_discardsTwoInvalidStationsAndKeepsTwoValidOnes() {
    // given
    Pair<JsonResponseFixture, JsonObject> fixtures = JsonResponseFixture.createFromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_2INVALID_2VALID);

    JsonResponseFixture objectFixture = fixtures.getLeft();
    JsonObject jsonFixture = fixtures.getRight();

    // when
    ArrayList<PetrolStation> actualValidPetrolStations = gson.fromJson(
        jsonFixture,
        (Type) PetrolStation.class);

    // then
    // Expect that 2 valid stations have been created.
    assertEquals(2, actualValidPetrolStations.size());

    // Expect exactly 2 collected error messages (of the 2 invalid fixtured stations)
    assertEquals(2, petrolStationsJsonAdapter.getErrorMessages().size());

    objectFixture.assertEquals(actualValidPetrolStations);
  }
}