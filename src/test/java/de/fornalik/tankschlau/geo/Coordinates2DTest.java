package de.fornalik.tankschlau.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class Coordinates2DTest {
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

  @Test
  void getLatitude() {
    // given
    Coordinates2D sut = new Coordinates2D(56.37463012, 0);

    // when
    double actualLatitude = sut.getLatitude();

    // then
    assertEquals(56.37463012, actualLatitude);
  }

  @Test
  void getLongitude() {
    // given
    Coordinates2D sut = new Coordinates2D(0, 152.0192834);

    // when
    double actualLongitude = sut.getLongitude();

    // then
    assertEquals(152.0192834, actualLongitude);
  }
}