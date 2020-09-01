package de.fornalik.tankschlau.geo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DistanceTest {

  @Test
  void constructor_shouldThrowOnNegativeKm() {
    assertThrows(
        IllegalArgumentException.class,
        () -> new Distance(-13.602)
    );
  }

  @Test
  void getKm_shouldReturnProperly() {
    // given
    Distance sut = new Distance(40.2837456412);

    // when
    double actualKm = sut.getKm();

    // then
    assertEquals(40.2837456412, actualKm);
  }

  @ParameterizedTest
  @ValueSource(doubles = {120.001, 30.0, 0, 1876252.3938438743})
  void setKm_shouldSetProperly(double validValue)
      throws NoSuchFieldException, IllegalAccessException {
    // given
    Distance sut = new Distance(0);

    // when
    sut.setKm(validValue);

    // then
    final Field field = sut.getClass().getDeclaredField("km");
    field.setAccessible(true);
    assertEquals(validValue, field.get(sut));
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