package de.fornalik.tankschlau.geo;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AddressTest {
  @Test
  void getCoordinates2D_happy() {
    // given
    Coordinates2D expectedCoordinates = new Coordinates2D(2.9372, -42.20236);
    Address sut = new Address(null, "x", null, "x", "x", expectedCoordinates);
    sut.setCoordinates2D(expectedCoordinates);

    // when then
    Coordinates2D actualCoordinates = sut
        .getCoordinates2D()
        .orElse(new Coordinates2D(0.0, 0.0));

    assertEquals(expectedCoordinates, actualCoordinates);
  }

  @Test
  void getCoordinates2D_shouldReturnEmptyOptional() {
    // given when
    Address sut = new Address("x", "x", "x");

    // when
    sut.setCoordinates2D(null);

    // then
    assertEquals(Optional.empty(), sut.getCoordinates2D());
  }
}