package de.fornalik.tankschlau.station;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.testhelp.response.FixtureFiles;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.testhelp.response.JsonResponseHelp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class PetrolStationsJsonAdapterTest {
  private static Gson gson;
  private static PetrolStationsJsonAdapter petrolStationsJsonAdapter;
  private JsonResponseHelp fixture;

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

  @BeforeEach
  void setUp() {
    fixture = new JsonResponseHelp();
  }

  @Test
  void read_oneStation_happy() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        fixture.jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertNotNull(actualPetrolStations);
    assertEquals(1, actualPetrolStations.size());
    assertEquals(0, petrolStationsJsonAdapter.getErrorMessages().size());
    fixture.assertEqualValuesIgnoringSort(actualPetrolStations);
  }

  @Test
  void read_oneStation_returnsEmptyArrayIfJsonArrayElementsAreNoJsonObjects() {
    // given
    FileReader reader = FixtureFiles.getFileReaderForResource(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_STATIONS_ARRAY_IS_STRING_ARRAY);

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        reader,
        (Type) PetrolStation.class);

    // then
    // Expect an empty PetrolStation array.
    assertEquals(0, actualPetrolStations.size());

    // Expect that one message is logged for each of the 3 JsonStrings in the
    // JsonArray of the fixture.
    assertEquals(3, petrolStationsJsonAdapter.getErrorMessages().size());
  }

  @Test
  void read_oneStation_returnsEmptyArrayOnMissingIdElement() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_MISSING_ID_ELEM);

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        fixture.jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertEquals(0, actualPetrolStations.size());
    assertEquals(1, petrolStationsJsonAdapter.getErrorMessages().size());
  }

  @Test
  void read_oneStation_acceptsEmptyBrand() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_HOUSENUM_AND_BRAND);

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        fixture.jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertEquals("", actualPetrolStations.get(0).brand);
    assertEquals(0, petrolStationsJsonAdapter.getErrorMessages().size());
  }

  @Test
  void read_noStations_returnsEmptyArrayOnMissingStationsElement() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_STATIONS_ELEM);

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        fixture.jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertEquals(0, actualPetrolStations.size());
    assertEquals(1, petrolStationsJsonAdapter.getErrorMessages().size());
  }

  @Test
  void read_emptyStations_returnsEmptyArrayOnEmptyStationsJsonArray() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_STATION_ARRAY);

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        fixture.jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertEquals(0, actualPetrolStations.size());
    assertEquals(1, petrolStationsJsonAdapter.getErrorMessages().size());
  }

  @Test
  void read_multipleStations_happy() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_17STATIONS_HAPPY);

    // when
    ArrayList<PetrolStation> actualPetrolStations = gson.fromJson(
        fixture.jsonFixture,
        (Type) PetrolStation.class);

    // then
    assertNotNull(actualPetrolStations);
    assertEquals(17, actualPetrolStations.size());
    assertEquals(0, petrolStationsJsonAdapter.getErrorMessages().size());
    fixture.assertEqualValuesIgnoringSort(actualPetrolStations);
  }

  @Test
  void read_multipleStations_discardsTwoInvalidStationsAndKeepsTwoValidOnes() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_2INVALID_2VALID);

    // when
    ArrayList<PetrolStation> actualValidPetrolStations = gson.fromJson(
        fixture.jsonFixture,
        (Type) PetrolStation.class);

    // then
    // Expect that 2 valid stations have been created.
    assertEquals(2, actualValidPetrolStations.size());

    // Expect exactly 2 collected error messages (of the 2 invalid fixed stations)
    assertEquals(2, petrolStationsJsonAdapter.getErrorMessages().size());
  }

  @Test
  void write_throwsUnsupportedOperationException() {
    // given
    PetrolStationsJsonAdapter adapter = new PetrolStationsJsonAdapter();

    // when then
    assertThrows(
        UnsupportedOperationException.class,
        () -> adapter.write(null, null));
  }
}