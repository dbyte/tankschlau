package de.fornalik.tankschlau.geo;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DistanceTest {

  @Test
  void getKm_shouldReturnValue() {
    // Given
    Distance sut = new Distance(40.2837456412);

    // When
    double actualKm = sut.getKm();

    // Then
    assertEquals(40.2837456412, actualKm);
  }

  @Test
  void setKm_shouldSetValue() {
    // Given
    Distance sut = new Distance(0);

    // When
    sut.setKm(120.001);

    // Then
    assertEquals(120.001,sut.getKm());
  }

  @Test
  void setKm_shouldThrowOnNegativeValue() {
    // Given
    Distance sut = new Distance(12.0);

    // When Then
    assertThrows(
        IllegalArgumentException.class,
        () -> sut.setKm(-0.4)
    );
  }
}