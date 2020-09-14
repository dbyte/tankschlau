package de.fornalik.tankschlau.geo;

import com.google.gson.JsonObject;
import de.fornalik.tankschlau.helpers.response.FixtureFiles;
import de.fornalik.tankschlau.helpers.response.JsonResponseFixture;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GeoTest {
  @Test
  void constructor_happy() {
    assertDoesNotThrow(() -> new Geo(-85.05112878, 64.03711, 23.56));
    assertDoesNotThrow(() -> new Geo(77.28374, -23.857));
  }

  @Test
  void constructor_doesNotThrowOnNullDistance() {
    assertDoesNotThrow(() -> new Geo(1, 1, null));
  }

  @Test
  void constructor_throwsOnInvalidCoordinates() {
    // invalid latitude
    assertThrows(
        Geo.InvalidGeoDataException.class,
        () -> new Geo(-85.05112879, 50.0)
    );
    assertThrows(
        Geo.InvalidGeoDataException.class,
        () -> new Geo(85.05112879, 50.0)
    );

    // invalid longitude
    assertThrows(
        Geo.InvalidGeoDataException.class,
        () -> new Geo(-70.0, -180.0001)
    );
    assertThrows(
        Geo.InvalidGeoDataException.class,
        () -> new Geo(-70.0, 180.0001)
    );
  }

  @Test
  void constructor_throwsOnInvalidDistance() {
    assertThrows(
        Geo.InvalidGeoDataException.class,
        () -> new Geo(1, 1, -0.01)
    );
  }

  // region createFromJson Tests

  /* Tests for Geo.createFromJson(JsonObject). The heavy load is currently done
  by a default Gson adapter. */

  @Test
  void createFromJson_happy() {
    // given
    Pair<JsonResponseFixture, JsonObject> fixtures = JsonResponseFixture.createOneGeoFromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    JsonResponseFixture responseFixture = fixtures.getLeft();
    JsonObject jsonFirstStationFixture = fixtures.getRight();

    // when
    Geo actualGeo = Geo.createFromJson(jsonFirstStationFixture);

    // then
    assertNotNull(actualGeo);
    responseFixture.assertEquals(actualGeo);
  }

  @Test
  void createFromJson_doesHandleMissingDistanceElement() {
    // given
    Pair<JsonResponseFixture, JsonObject> fixtures = JsonResponseFixture.createOneGeoFromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_DIST_ELEM);

    JsonObject jsonFirstStationFixture = fixtures.getRight();

    // when
    Geo actualGeo = Geo.createFromJson(jsonFirstStationFixture);

    // then
    assertNotNull(actualGeo);
    assertEquals(Optional.empty(), actualGeo.getDistance());
  }

  @Test
  void createFromJson_setsLatLonToZeroOnMissingLatLonElements() {
    // given
    Pair<JsonResponseFixture, JsonObject> fixtures = JsonResponseFixture.createOneGeoFromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_LAT_LON_ELEM);

    JsonObject jsonFirstStationFixture = fixtures.getRight();

    // when
    Geo actualGeo = Geo.createFromJson(jsonFirstStationFixture);

    // then
    assertNotNull(actualGeo);
    assertEquals(0.0, actualGeo.latitude);
    assertEquals(0.0, actualGeo.longitude);
  }

  @Test
  void createFromJson_doesThrowWhenPassingNullToIt() {
    assertThrows(
        NullPointerException.class,
        () -> Geo.createFromJson(null)
    );
  }

  // endregion

  @Test
  void getDistance_happy() {
    // given
    final Double expectedDistance = 524.203826323;
    final Geo geo = new Geo(1, 1, expectedDistance);

    // when
    Double actualDistance = geo.getDistance().orElse(0.0);

    // then
    assertEquals(expectedDistance, actualDistance);
  }

  @Test
  void getDistance_returnsEmptyOptionalOnNullDistance() {
    // given
    final Geo geo = new Geo(1, 1);
    geo.setDistance(null);

    // when
    Optional<Double> actualValue = geo.getDistance();

    // then
    assertEquals(Optional.empty(), actualValue);
  }

  @Test
  void setDistance_throwOnInvalidDistance() {
    // given
    final double invalidNegativeDistance = -5.294;
    final Geo geo = new Geo(0.0, 0.0);

    // when then
    assertThrows(
        Geo.InvalidGeoDataException.class,
        () -> geo.setDistance(invalidNegativeDistance)
    );
  }

  @Test
  void setDistance_acceptsNull() {
    // given
    final Geo geo = new Geo(0.0, 0.0);

    // when then
    assertDoesNotThrow(() -> geo.setDistance(null));
  }

  @ParameterizedTest
  @CsvSource(value = {
      "27.1863101;43.82546;58.324234",
      "1;2;3",
      "1;2;null"},
      delimiter = ';')
  void equals_returnsTrue(double lat, double lon, String distAsString) {
    // given
    final Double dist = distAsString.equals("null") ? null : Double.parseDouble(distAsString);
    final Geo geo_1st = new Geo(lat, lon, dist);
    final Geo geo_2nd = new Geo(lat, lon, dist);

    // when
    final boolean equalityProven = geo_1st.equals(geo_2nd);

    // then
    assertTrue(equalityProven);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "1;0;3.0",
      "0;2;3.0",
      "1;2;null",
      "40;60;80.0"},
      delimiter = ';')
  void equals_returnsFalse(double lat, double lon, String distAsString) {
    // given
    final Double dist = distAsString.equals("null") ? null : Double.parseDouble(distAsString);
    final Geo geo_1st = new Geo(lat, lon, dist);
    final Geo geo_2nd = new Geo(1, 2, 3.0);

    // when
    final boolean equalityProven = geo_1st.equals(geo_2nd);

    // then
    assertFalse(equalityProven);
  }

  @Test
  void toString_doesNotThrow() {
    // given
    Geo geo = new Geo(27, 97.02479273623, 1.00023735118365);
    // when then
    assertDoesNotThrow(geo::toString);

    // given
    geo = new Geo(0, 0, null);
    // when then
    assertDoesNotThrow(geo::toString);
  }
}