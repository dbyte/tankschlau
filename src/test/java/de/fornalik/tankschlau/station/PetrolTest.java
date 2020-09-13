package de.fornalik.tankschlau.station;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PetrolTest {

  @Test
  void equals_returnsTrue() {
    // given
    Petrol petrol_1st = new Petrol(PetrolType.E5, 1.299);
    Petrol petrol_2nd = new Petrol(PetrolType.E5, 1.299);

    // when
    final boolean equalityProven = petrol_1st.equals(petrol_2nd);

    // then
    assertTrue(equalityProven);
  }

  @Test
  void equals_returnsFalse() {
    // given
    Petrol petrol_1st = new Petrol(PetrolType.DIESEL, 0.779);
    Petrol petrol_2nd = new Petrol(PetrolType.DIESEL, 1.299);
    // when
    boolean equalityProven = petrol_1st.equals(petrol_2nd);
    // then
    assertFalse(equalityProven);

    // given
    petrol_1st = new Petrol(PetrolType.DIESEL, 1.0);
    petrol_2nd = new Petrol(PetrolType.E10, 1.0);
    // when
    equalityProven = petrol_1st.equals(petrol_2nd);
    // then
    assertFalse(equalityProven);
  }

  @Test
  void toString_doesNotThrow() {
    // given
    Petrol petrol = new Petrol(PetrolType.E5, 1.397);
    // when then
    assertDoesNotThrow(petrol::toString);
  }
}