package de.fornalik.tankschlau.geo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.testhelp.response.FixtureFiles;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.testhelp.response.JsonFixtureTestsuite;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AddressJsonAdapterTest extends JsonFixtureTestsuite {
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
    setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    // when
    Address actualAddress = gson.fromJson(jsonFixture, Address.class);

    // then
    assertNotNull(actualAddress);
    objectFixture.assertEqualValues(actualAddress, 0);
  }

  @Test
  void read_doesNotSetGeoIfAllGeoDataIsMissing() {
    // given
    setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_ALL_GEO_ELEM);

    // when
    Address actualAddress = gson.fromJson(jsonFixture, Address.class);

    // then
    assertEquals(Optional.empty(), actualAddress.getGeo());
  }

  @Test
  void read_doesSetGeoIfLatLonIsInJsonWhileDistanceIsMissing() {
    // given
    setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_DIST_ELEM);

    // when
    Address actualAddress = gson.fromJson(jsonFixture, Address.class);

    // then
    assertNotEquals(Optional.empty(), actualAddress.getGeo());
  }

  @Test
  void read_doesSetGeoAndSetsLatLonToZeroIfDistanceIsInJsonWhileLatLonIsMissing() {
    // given
    setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_LAT_LON_ELEM);

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
    setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_STREET_AND_PLACE_AND_POSTCODE);

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