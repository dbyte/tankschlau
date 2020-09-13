package de.fornalik.tankschlau.geo;

import de.fornalik.tankschlau.util.StringLegalizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

  @ParameterizedTest
  @CsvSource(value = {
      ":::",
      "Some street::",
      "Baba Street:Berlin:",
      " : : ",
      "Yoda Street: : ",
      "Good Street:München: ",
      ": :Frankfurt"},
      delimiter = ':')
  void constructor_throwsOnInvalidMandatoryValues(String s1, String s2, String s3) {
    // given
    Map<String, String> mandatoryFields = new HashMap<>();
    mandatoryFields.put("street", s1);
    mandatoryFields.put("city", s2);
    mandatoryFields.put("postCode", s3);

    // when then
    assertThrows(
        StringLegalizer.ValueException.class,
        () -> new Address(
            mandatoryFields.get("street"),
            mandatoryFields.get("city"),
            mandatoryFields.get("postCode")),

        String.format("Test data: %s", mandatoryFields.toString())
    );
  }

  @Test
  void constructor_acceptsNullForCoordinates() {
    // when then
    assertDoesNotThrow(() -> new Address("x", "y", "z", null));
  }

  @Test
  void constructor_acceptsEmptyStringsForOptionalFields() {
    // when then
    assertDoesNotThrow(() -> new Address("", "Street name", "", "City name", "Post code", null));
  }

  @Test
  void setName_legalizesValue() {
    // given
    Address address = new Address("x", "y", "z");

    // when
    address.setName("   My Name To Be Trimmed            ");
    // then
    assertEquals("My Name To Be Trimmed", address.getName());

    // when
    address.setName(null);
    // then
    assertEquals("", address.getName());
  }

  @Test
  void setHouseNumber_legalizesValue() {
    // given
    Address address = new Address("x", "y", "z");

    // when
    address.setHouseNumber("      25 B Hinterhof  ");
    // then
    assertEquals("25 B Hinterhof", address.getHouseNumber());

    // when
    address.setHouseNumber(null);
    // then
    assertEquals("", address.getHouseNumber());
  }

  @Test
  void setStreet_legalizesValue() {
    // given
    Address address = new Address("x", "y", "z");

    // when
    address.setStreet("Walter von Schön Straße   ");
    // then
    assertEquals("Walter von Schön Straße", address.getStreet());

    // Test that throws on empty string
    // when then
    assertThrows(
        StringLegalizer.ValueException.class,
        () -> address.setStreet(""));
  }

  @Test
  void setCity_legalizesValue() {
    // given
    Address address = new Address("x", "y", "z");

    // when
    address.setCity(" Düsseldorf   ");
    // then
    assertEquals("Düsseldorf", address.getCity());

    // Test that throws on empty string
    // when then
    assertThrows(
        StringLegalizer.ValueException.class,
        () -> address.setCity(""));
  }

  @Test
  void setPostCode_legalizesValue() {
    // given
    Address address = new Address("x", "y", "z");

    // when
    address.setPostCode("   D-80803");
    // then
    assertEquals("D-80803", address.getPostCode());

    // Test that throws on empty string
    // when then
    assertThrows(
        StringLegalizer.ValueException.class,
        () -> address.setPostCode(""));
  }

  @Test
  void getGeo_happy() {
    // given
    Geo expectedGeo = new Geo(2.9372, -42.20236, 25.284746);
    Address address = new Address("x", "y", "z", expectedGeo);
    address.setGeo(expectedGeo);

    // when
    Geo actualCoordinates = address
        .getGeo()
        .orElse(new Geo(0.0, 0.0, null));

    // then
    assertEquals(expectedGeo, actualCoordinates);
  }

  @Test
  void getGeo_returnsEmptyOptional() {
    // given
    Address address = new Address("x", "y", "z");

    // when
    address.setGeo(null);

    // then
    assertEquals(Optional.empty(), address.getGeo());
  }

  @Test
  void toString_doesNotThrowOnMinimumInitialization() {
    // given
    Address addressWithNulls = new Address("x", "y", "z");
    addressWithNulls.setGeo(null);

    // when then
    assertDoesNotThrow(addressWithNulls::toString);
  }
}