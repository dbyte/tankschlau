package de.fornalik.tankschlau.geo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.testhelp.response.FixtureFiles;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.testhelp.response.JsonResponseHelp;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AddressJsonAdapterTest {
  private static Gson gson;

  @BeforeAll
  static void beforeAll() {
    gson = new GsonBuilder()
        .registerTypeAdapter(Address.class, new AddressJsonAdapter())
        .create();
  }

  @AfterAll
  static void afterAll() {
    gson = null;
  }

  @Test
  void read_happy() {
    // given
    Pair<JsonResponseHelp, JsonObject> responseHelp =
        JsonResponseHelp.createFirstStationFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    JsonResponseHelp objectFixture = responseHelp.getLeft();
    JsonObject jsonFixture = responseHelp.getRight();

    // when
    Address actualAddress = gson.fromJson(jsonFixture, Address.class);

    // then
    assertNotNull(actualAddress);
    objectFixture.assertEqualValues(actualAddress, 0);
  }

  @Test
  void read_doesNotSetGeoIfAllGeoDataIsMissing() {
    // given
    Pair<JsonResponseHelp, JsonObject> responseHelp =
        JsonResponseHelp.createFirstStationFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_ALL_GEO_ELEM);

    JsonObject jsonFixture = responseHelp.getRight();

    // when
    Address actualAddress = gson.fromJson(jsonFixture, Address.class);

    // then
    assertEquals(Optional.empty(), actualAddress.getGeo());
  }

  @Test
  void read_doesSetGeoIfLatLonIsInJsonWhileDistanceIsMissing() {
    // given
    Pair<JsonResponseHelp, JsonObject> responseHelp =
        JsonResponseHelp.createFirstStationFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_DIST_ELEM);

    JsonObject jsonFixture = responseHelp.getRight();

    // when
    Address actualAddress = gson.fromJson(jsonFixture, Address.class);

    // then
    assertNotEquals(Optional.empty(), actualAddress.getGeo());
  }

  @Test
  void read_doesSetGeoAndSetsLatLonToZeroIfDistanceIsInJsonWhileLatLonIsMissing() {
    // given
    Pair<JsonResponseHelp, JsonObject> responseHelp =
        JsonResponseHelp.createFirstStationFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_LAT_LON_ELEM);

    JsonObject jsonFixture = responseHelp.getRight();

    // when
    Address actualAddress = gson.fromJson(jsonFixture, Address.class);

    // then
    assertNotEquals(Optional.empty(), actualAddress.getGeo());

    //noinspection OptionalGetWithoutIsPresent
    assertEquals(0.0, actualAddress.getGeo().get().getLatitude());
    assertEquals(0.0, actualAddress.getGeo().get().getLongitude());
  }

  @Test
  void read_throwsJsonParseExceptionIfMandatoryDataAreMissing() {
    // given
    Pair<JsonResponseHelp, JsonObject> responseHelp =
        JsonResponseHelp.createFirstStationFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_STREET_AND_PLACE_AND_POSTCODE);

    JsonObject jsonFixture = responseHelp.getRight();

    // when then
    assertThrows(
        JsonParseException.class,
        () -> gson.fromJson(jsonFixture, Address.class));
  }

  @Test
  void write_throwsUnsupportedOperationException() {
    // given
    AddressJsonAdapter adapter = new AddressJsonAdapter();

    // when then
    assertThrows(
        UnsupportedOperationException.class,
        () -> adapter.write(null, null));
  }
}