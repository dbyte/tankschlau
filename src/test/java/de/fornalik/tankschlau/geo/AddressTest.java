package de.fornalik.tankschlau.geo;

import com.google.gson.JsonObject;
import de.fornalik.tankschlau.helpers.response.FixtureFiles;
import de.fornalik.tankschlau.helpers.response.JsonResponseHelp;
import de.fornalik.tankschlau.util.StringLegalizer;
import org.apache.commons.lang3.tuple.Pair;
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
      "::",
      "Some street::",
      "Baba Street:Berlin:",
      " : : ",
      "Yoda Street: : ",
      "Good Street:München: ",
      ": :Frankfurt"},
      delimiter = ':')
  void constructor_throwsOnInvalidMandatoryValues(String s1, String s2, String s3) {
    // given
    final Map<String, String> mandatoryFields = new HashMap<>();
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
  void constructor_acceptsNullForGeo() {
    // when then
    assertDoesNotThrow(() -> new Address("x", "y", "z", (Geo) null));
  }

  @Test
  void constructor_acceptsEmptyStringsForOptionalFields() {
    // when then
    assertDoesNotThrow(() -> new Address("", "Street name", "", "City name", "Post code", null));
  }

  // region createFromJson Tests

  /* Tests for Address.createFromJson(JsonObject). The heavy load is currently done
  by AddressJsonAdapter, which is not part of this unit and tested separately. */

  @Test
  void createFromJson_happy() {
    // given
    Pair<JsonResponseHelp, JsonObject> responseHelp =
        JsonResponseHelp.createFirstStationFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    JsonResponseHelp responseFixture = responseHelp.getLeft();
    JsonObject jsonFirstStationFixture = responseHelp.getRight();

    // when
    Address actualAddress = Address.createFromJson(jsonFirstStationFixture);

    // then
    assertNotNull(actualAddress);
    responseFixture.assertEqualsIgnoringSort(actualAddress, 0);
  }

  @Test
  void createFromJson_acceptsEmptyHouseNumber() {
    // given
    Pair<JsonResponseHelp, JsonObject> responseHelp =
        JsonResponseHelp.createFirstStationFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_HOUSENUM_AND_BRAND);

    JsonObject jsonFirstStationFixture = responseHelp.getRight();

    // when
    Address actualAddress = Address.createFromJson(jsonFirstStationFixture);

    // then
    assertEquals("", actualAddress.getHouseNumber());
  }

  @Test
  void createFromJson_returnsEmptyOptionalGeoObjectIfAllGeoElementsAreMissing() {
    // given
    Pair<JsonResponseHelp, JsonObject> responseHelp =
        JsonResponseHelp.createFirstStationFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_ALL_GEO_ELEM);

    JsonObject jsonFirstStationFixture = responseHelp.getRight();

    // when
    Address actualAddress = Address.createFromJson(jsonFirstStationFixture);

    // then
    assertNotNull(actualAddress);
    assertEquals(Optional.empty(), actualAddress.getGeo());
  }

  // endregion

  @Test
  void setName_legalizesValue() {
    // given
    final Address address = new Address("x", "y", "z");

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
    final Address address = new Address("x", "y", "z");

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
    final Address address = new Address("x", "y", "z");

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
    final Address address = new Address("x", "y", "z");

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
    final Address address = new Address("x", "y", "z");

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

  @ParameterizedTest
  @CsvSource(value = {
      "2.9372;-42.20236;25.284746",
      "30.0;40.0;null"},
      delimiter = ';')
  void getGeo_happy(double lat, double lon, String distAsString) {
    // given
    final Double dist = distAsString.equals("null") ? null : Double.parseDouble(distAsString);
    final Geo expectedGeo = new Geo(lat, lon, dist);
    final Address address = new Address("x", "y", "z", expectedGeo);

    // when
    Geo actualGeoObject = address
        .getGeo()
        .orElse(new Geo(0.0, 0.0, 999999.0));

    // then
    assertEquals(expectedGeo, actualGeoObject);
  }

  @Test
  void getGeo_returnsEmptyOptional() {
    // given
    final Address address = new Address("x", "y", "z");

    // when
    address.setGeo(null);

    // then
    assertEquals(Optional.empty(), address.getGeo());
  }

  @Test
  void toString_doesNotThrowOnMinimumInitialization() {
    // given
    final Address addressWithNullGeo = new Address("x", "y", "z");
    addressWithNullGeo.setGeo(null);

    // when then
    assertDoesNotThrow(addressWithNullGeo::toString);
  }

  @Test
  void toString_doesNotThrow() {
    // given
    final Address addressWithNullGeo = new Address("x", "y", "z");
    // when then
    assertDoesNotThrow(addressWithNullGeo::toString);

    // given
    final Geo fullGeo = new Geo(43.4, 79.355, 303.0);
    final Address completeAddress = new Address(
        "a", "b", "1", "c", "80803", fullGeo);

    // when then
    assertDoesNotThrow(completeAddress::toString);
  }
}