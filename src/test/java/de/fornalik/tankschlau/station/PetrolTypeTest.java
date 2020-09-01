package de.fornalik.tankschlau.station;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PetrolTypeTest {
  @Test
  void countOfItemsIsCorrect() {
    assertEquals(3, PetrolType.values().length);
  }

  @Test
  void namingIsCorrect() {
    assertEquals(PetrolType.DIESEL, PetrolType.valueOf("DIESEL"));
    assertEquals(PetrolType.E5, PetrolType.valueOf("E5"));
    assertEquals(PetrolType.E10, PetrolType.valueOf("E10"));
  }
}