package de.fornalik.tankschlau.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GeoLocationTest {
  @Test
  void getCoordinates2D_happy() throws GeoLocation.CoordinatesNullException {
    // given
    Coordinates2D expectedCoordinates = new Coordinates2D(2.9372, -42.20236);
    GeoLocation sut = new GeoLocation(expectedCoordinates);

    // when then
    Coordinates2D actualCoordinates = sut.getCoordinates2D();
    assertEquals(expectedCoordinates, actualCoordinates);
  }

  @Test
  void getCoordinates2D_shouldThrowIfNull() {
    // given
    GeoLocation sut = new GeoLocation(null);

    // when then
    assertThrows(
        GeoLocation.CoordinatesNullException.class,
        sut::getCoordinates2D
    );
  }
}