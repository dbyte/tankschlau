package de.fornalik.tankschlau.station;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PetrolTypeTest {
  @Test
  void countOfItemsIsCorrect() {
    assertEquals(3, PetrolType.values().length);
  }

  @Test
  void namingIsCorrect() {
    assertEquals(PetrolType.E10, PetrolType.valueOf("E10"));
    assertEquals(PetrolType.E5, PetrolType.valueOf("E5"));
    assertEquals(PetrolType.DIESEL, PetrolType.valueOf("DIESEL"));
  }

  @Test
  void getJsonKey_happy() {
    assertEquals("e10", PetrolType.E10.getJsonKey());
    assertEquals("e5", PetrolType.E5.getJsonKey());
    assertEquals("diesel", PetrolType.DIESEL.getJsonKey());
  }

  @Test
  void getReadableName_happy() {
    assertEquals("E10", PetrolType.E10.getReadableName());
    assertEquals("E5", PetrolType.E5.getReadableName());
    assertEquals("Diesel", PetrolType.DIESEL.getReadableName());
  }
}