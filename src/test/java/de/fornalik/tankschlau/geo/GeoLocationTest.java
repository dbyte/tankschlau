package de.fornalik.tankschlau.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoLocationTest {
  @Test
  void getCoordinates2D_happy() {
    // given
    Coordinates2D expectedCoordinates = new Coordinates2D(2.9372, -42.20236);
    GeoLocation sut = new GeoLocation();
    sut.setCoordinates2D(expectedCoordinates);

    // when then
    Coordinates2D actualCoordinates = sut.getCoordinates2D();
    assertEquals(expectedCoordinates, actualCoordinates);
  }

  @Test
  void getCoordinates2D_shouldZeroIfSetToNull() {
    // given
    Coordinates2D expectedCoordinates = new Coordinates2D(0.0, 0.0);
    GeoLocation sut = new GeoLocation();

    // when
    sut.setCoordinates2D(null);

    // then
    assertEquals(expectedCoordinates, sut.getCoordinates2D());
  }
}