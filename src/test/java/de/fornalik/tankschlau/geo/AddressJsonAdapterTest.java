package de.fornalik.tankschlau.geo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import de.fornalik.tankschlau.testhelp_common.DomainFixtureHelp;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AddressJsonAdapterTest {
  private static Gson gson;
  private DomainFixtureHelp fixture;

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

  @BeforeEach
  void setUp() {
    fixture = new DomainFixtureHelp();
  }

  @Test
  void read_happy() {
    // given
    fixture.setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    // when
    Address actualAddress = gson.fromJson(fixture.jsonFixture, Address.class);

    // then
    assertNotNull(actualAddress);
    fixture.assertEqualValues(actualAddress, 0);
  }

  @Test
  void read_doesNotSetGeoIfAllGeoDataIsMissing() {
    // given
    fixture.setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_ALL_GEO_ELEM);

    // when
    Address actualAddress = gson.fromJson(fixture.jsonFixture, Address.class);

    // then
    assertEquals(Optional.empty(), actualAddress.getGeo());
  }

  @Test
  void read_doesSetGeoIfLatLonIsInJsonWhileDistanceIsMissing() {
    // given
    fixture.setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_DIST_ELEM);

    // when
    Address actualAddress = gson.fromJson(fixture.jsonFixture, Address.class);

    // then
    assertNotEquals(Optional.empty(), actualAddress.getGeo());
  }

  @Test
  void read_doesSetGeoAndSetsLatLonToZeroIfDistanceIsInJsonWhileLatLonIsMissing() {
    // given
    fixture.setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_LAT_LON_ELEM);

    // when
    Address actualAddress = gson.fromJson(fixture.jsonFixture, Address.class);

    // then
    assertNotEquals(Optional.empty(), actualAddress.getGeo());

    //noinspection OptionalGetWithoutIsPresent
    assertEquals(0.0, actualAddress.getGeo().get().getLatitude());
    assertEquals(0.0, actualAddress.getGeo().get().getLongitude());
  }

  @Test
  void read_throwsJsonParseExceptionIfMandatoryDataAreMissing() {
    // given
    fixture.setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_STREET_AND_PLACE_AND_POSTCODE);

    // when then
    assertThrows(
        JsonParseException.class,
        () -> gson.fromJson(fixture.jsonFixture, Address.class));
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