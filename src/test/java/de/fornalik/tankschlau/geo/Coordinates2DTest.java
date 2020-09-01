package de.fornalik.tankschlau.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Coordinates2DTest {
  @Test
  void constructor_happy() {
    assertDoesNotThrow(() -> new Coordinates2D(-85.05112878, 64.03711));
  }

  @Test
  void constructor_shouldThrowOnInvalidCoordinates() {
    // invalid latitude
    assertThrows(
        Coordinates2D.InvalidCoordinatesException.class,
        () -> new Coordinates2D(-85.05112879, 50.0)
    );
    assertThrows(
        Coordinates2D.InvalidCoordinatesException.class,
        () -> new Coordinates2D(85.05112879, 50.0)
    );

    // invalid longitude
    assertThrows(
        Coordinates2D.InvalidCoordinatesException.class,
        () -> new Coordinates2D(-70.0, -180.0001)
    );
    assertThrows(
        Coordinates2D.InvalidCoordinatesException.class,
        () -> new Coordinates2D(-70.0, 180.0001)
    );
  }
}