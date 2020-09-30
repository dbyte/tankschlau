package de.fornalik.tankschlau.util;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.prefs.BackingStoreException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for UserPrefs. We execute combined write/read tests.
 */
class UserPrefsTest {
  private static Address addressMock;
  private static Geo geoMock;
  private UserPrefs prefs;

  @BeforeAll
  static void beforeAll() {
    addressMock = Mockito.mock(Address.class);
    geoMock = Mockito.mock(Geo.class);

    Mockito.when(geoMock.getLatitude()).thenReturn(50.1234);
    Mockito.when(geoMock.getLongitude()).thenReturn(10.5678);
    Mockito.when(geoMock.getDistance()).thenReturn(Optional.of(12.5));

    Mockito.when(addressMock.getName()).thenReturn("OMV Berlin");
    Mockito.when(addressMock.getCity()).thenReturn("Berlin");
    Mockito.when(addressMock.getPostCode()).thenReturn("10000");
    Mockito.when(addressMock.getStreet()).thenReturn("Grötzmützelweg");
    Mockito.when(addressMock.getHouseNumber()).thenReturn("10b");
    Mockito.when(addressMock.getGeo()).thenReturn(Optional.ofNullable(geoMock));
  }

  @AfterAll
  static void afterAll() {
    addressMock = null;
    geoMock = null;
  }

  @BeforeEach
  void setUpEach() {
    prefs = new UserPrefs(this.getClass());
  }

  @AfterEach
  void tearDownEach() throws BackingStoreException {
    // Delete node of persisted test-preferences
    if (prefs.getRealPrefs().nodeExists(""))
      prefs.getRealPrefs().removeNode();
  }

  @Test
  void writeAddress_writesProperly() {
    // given
    Mockito.when(addressMock.getGeo()).thenReturn(Optional.empty());

    // when
    prefs.writeAddress(addressMock);
    Optional<Address> actualAddress = prefs.readAddress();

    // then
    assertTrue(actualAddress.isPresent());
    assertEquals(addressMock.getName(), actualAddress.get().getName());
    assertEquals(addressMock.getCity(), actualAddress.get().getCity());
    assertEquals(addressMock.getPostCode(), actualAddress.get().getPostCode());
    assertEquals(addressMock.getStreet(), actualAddress.get().getStreet());
    assertEquals(addressMock.getHouseNumber(), actualAddress.get().getHouseNumber());
  }

  @Test
  void readAddress_returnsEmptyOptionalIfAddressPrefsDoNotExist() {
    // when
    Optional<Address> actualAddress = prefs.readAddress();

    // then
    assertEquals(Optional.empty(), actualAddress);
  }

  @Test
  void readAddress_returnsEmptyOptionalIfNodeDoesNotExist() throws BackingStoreException {
    // given
    prefs.getRealPrefs().removeNode();

    // when
    Optional<Address> actualAddress = prefs.readAddress();

    // then
    assertEquals(Optional.empty(), actualAddress);
  }

  @Test
  void writeGeo_writesProperly() {
    // when
    prefs.writeGeo(geoMock);
    Optional<Geo> optionalGeo = prefs.readGeo();

    // then
    assertTrue(optionalGeo.isPresent());
    Geo actualGeo = optionalGeo.get();

    assertEquals(geoMock.getLatitude(), actualGeo.getLatitude());
    assertEquals(geoMock.getLongitude(), actualGeo.getLongitude());
    assertEquals(geoMock.getDistance(), actualGeo.getDistance());
  }

  @Test
  void readGeo_returnsEmptyOptionalIfGeoLatLonDoNotExist() {
    // when
    Optional<Geo> actualGeo = prefs.readGeo();

    // then
    assertEquals(Optional.empty(), actualGeo);
  }

  @Test
  void readGeo_returnsEmptyOptionalIfNodeDoesNotExist() throws BackingStoreException {
    // given
    prefs.getRealPrefs().removeNode();

    // when
    Optional<Geo> actualGeo = prefs.readGeo();

    // then
    assertEquals(Optional.empty(), actualGeo);
  }

  @ParameterizedTest
  @EnumSource(PetrolType.class)
  void writePreferredPetrolType_writesProperly(PetrolType givenPetrolType) {
    // when
    prefs.writePreferredPetrolType(givenPetrolType);
    Optional<PetrolType> actualPetrolType = prefs.readPreferredPetrolType();

    // then
    assertTrue(actualPetrolType.isPresent());
    assertEquals(givenPetrolType, actualPetrolType.get());
  }

  @Test
  void readPreferredPetrolType_returnsEmptyOptionalIfPrefDoesNotExist() {
    // when
    Optional<PetrolType> actualPetrolType = prefs.readPreferredPetrolType();

    // then
    assertEquals(Optional.empty(), actualPetrolType);
  }

  @Test
  void readPreferredPetrolType_returnsEmptyOptionalIfNodeDoesNotExist()
  throws BackingStoreException {
    // given
    prefs.getRealPrefs().removeNode();

    // when
    Optional<PetrolType> actualPetrolType = prefs.readPreferredPetrolType();

    // then
    assertEquals(Optional.empty(), actualPetrolType);
  }
}