package de.fornalik.tankschlau.geo;

import de.fornalik.tankschlau.util.Localization;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

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

  @Test
  void getDistanceAwayString_returnsExpectedString() {
    // given
    String actualString;
    final String locBaseName = "LocaleTestStrings";
    Localization locFixture = Localization.getInstance();
    Geo givenGeo;

    givenGeo = new Geo(54.354532, 23.0, 8.27456);
    locFixture.configure(Locale.GERMAN, ResourceBundle.getBundle(locBaseName, Locale.GERMAN));
    // when
    actualString = givenGeo.getDistanceAwayString();
    // then
    assertEquals("8,27 km entfernt", actualString);

    // given
    givenGeo = new Geo(54.354532, 23.0, 1.45);
    // when
    actualString = givenGeo.getDistanceAwayString();
    // then
    assertEquals("1,45 km entfernt", actualString);

    // given
    givenGeo = new Geo(54.354532, 23.0, 54.2);
    locFixture.configure(Locale.ENGLISH, ResourceBundle.getBundle(locBaseName, Locale.ENGLISH));
    // when
    actualString = givenGeo.getDistanceAwayString();
    // then
    assertEquals("54.2 km away", actualString);
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