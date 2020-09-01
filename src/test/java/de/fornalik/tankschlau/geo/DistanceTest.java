package de.fornalik.tankschlau.geo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class DistanceTest {
  @Test
  void constructor_happy() {
    assertDoesNotThrow(() -> new Distance(2.9324));
  }

  @Test
  void constructor_shouldThrowOnNegativeKm() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Distance(-13.602)
    );
  }

  @ParameterizedTest
  @ValueSource(doubles = {120.001, 30.0, 0, 1876252.3938438743})
  void setKm_happy(double validValue) {
    // given
    Distance sut = new Distance(0);

    // when
    sut.setKm(validValue);

    // then
    assertEquals(validValue, sut.getKm());
  }

  @ParameterizedTest
  @ValueSource(doubles = {-0.04, -33, -234867762.342234219})
  void setKm_shouldThrowOnNegativeValue(double negativeValue) {
    // given
    Distance sut = new Distance(12.0);

    // when then
    assertThrows(
        IllegalArgumentException.class,
        () -> sut.setKm(negativeValue)
    );
  }
}