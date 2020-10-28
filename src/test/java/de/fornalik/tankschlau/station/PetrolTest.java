package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.util.Localization;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class PetrolTest {

  @BeforeAll
  static void beforeAll() {
    // We must configure the Singleton here
    Localization.getInstance().configure(Locale.GERMANY);
  }

  @Test
  void getTypeAndPrice_returnsConcatenatedStringForGermanLocale() {
    // given
    Map<String, Petrol> givenPetrols = new TreeMap<>();
    givenPetrols.put("Diesel 0,978\u00A0\u20AC", new Petrol(PetrolType.DIESEL, 0.978));
    givenPetrols.put("E5 1,00\u00A0\u20AC", new Petrol(PetrolType.E5, 1.00));
    givenPetrols.put("E10 0,00\u00A0\u20AC", new Petrol(PetrolType.E10, 0));

    // when
    String actualString;
    for (Map.Entry<String, Petrol> entry : givenPetrols.entrySet()) {
      actualString = entry.getValue().getTypeAndPrice();

      // then
      assertEquals(entry.getKey(), actualString);
    }
  }

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