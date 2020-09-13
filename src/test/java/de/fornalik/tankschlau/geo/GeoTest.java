package de.fornalik.tankschlau.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GeoTest {
  @Test
  void constructor_happy() {
    assertDoesNotThrow(() -> new Geo(-85.05112878, 64.03711));
  }

  @Test
  void constructor_shouldThrowOnInvalidCoordinates() {
    // invalid latitude
    assertThrows(
        Geo.InvalidCoordinatesException.class,
        () -> new Geo(-85.05112879, 50.0)
    );
    assertThrows(
        Geo.InvalidCoordinatesException.class,
        () -> new Geo(85.05112879, 50.0)
    );

    // invalid longitude
    assertThrows(
        Geo.InvalidCoordinatesException.class,
        () -> new Geo(-70.0, -180.0001)
    );
    assertThrows(
        Geo.InvalidCoordinatesException.class,
        () -> new Geo(-70.0, 180.0001)
    );
  }
}