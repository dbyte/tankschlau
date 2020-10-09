package de.fornalik.tankschlau.geo;

import de.fornalik.tankschlau.util.StringLegalizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class AddressTest {
  private Address address;

  @BeforeEach
  void setUp() {
    address = null;
  }

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

  @Test
  void setName_legalizesValue() {
    // given
    address = new Address("x", "y", "z");

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
    address = new Address("x", "y", "z");

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
    address = new Address("x", "y", "z");

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
    address = new Address("x", "y", "z");

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
    address = new Address("x", "y", "z");

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
    address = new Address("x", "y", "z", expectedGeo);

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
    address = new Address("x", "y", "z");

    // when
    address.setGeo(null);

    // then
    assertEquals(Optional.empty(), address.getGeo());
  }

  @ParameterizedTest
  @CsvSource(value = {
      " Mockenmuck :7b:Mockenmuck 7b",
      " Freudenstraße ::Freudenstraße",
      "Homestreet: 23 b :Homestreet 23 b",
  }, delimiter = ':')
  void getStreetAndHouseNumber_returnsExpectedString(
      String street,
      String houseNumber,
      String expected) {

    // given
    String actualString;

    address = new Address(street, houseNumber, "X", "X");
    // when
    actualString = address.getStreetAndHouseNumber();
    // then
    assertEquals(expected, actualString);
  }

  /*@Test
  void setGeo_withWebService_doesProperlySetGeoOwnedByAddress() throws IOException {
    // given
    Address = new Address("don't care", "don't care", "don't care", "don't care");
    assert !address.getGeo().isPresent();

    Geo geoMock = mock(Geo.class);
    when(geoMock.getLatitude()).thenReturn(50.1078234);
    when(geoMock.getLongitude()).thenReturn(8.5413809);
    when(geoMock.getDistance()).thenReturn(Optional.empty());

    GoogleGeocodingClient geoCodingClientMock = mock(GoogleGeocodingClient.class);
    when(geoCodingClientMock.getGeo(address)).thenReturn(Optional.of(geoMock));

    // when
    address.setGeo(geoCodingClientMock);

    // then
    assertEquals(geoMock.getLatitude(), address.getGeo().get().getLatitude());
    assertEquals(geoMock.getLongitude(), address.getGeo().get().getLongitude());
  }*/

  @Test
  void toString_doesNotThrowOnMinimumInitialization() {
    // given
    address = new Address("x", "y", "z");
    address.setGeo(null);

    // when then
    assertDoesNotThrow(address::toString);
  }

  @Test
  void toString_doesNotThrow() {
    // given
    address = new Address("x", "y", "z");
    // when then
    assertDoesNotThrow(address::toString);

    // given
    final Geo fullGeo = new Geo(43.4, 79.355, 303.0);
    final Address completeAddress = new Address(
        "a", "b", "1", "c", "80803", fullGeo);

    // when then
    assertDoesNotThrow(completeAddress::toString);
  }
}