package de.fornalik.tankschlau.util;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;
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
    // It's important to use a class within a different package than
    // de.fornalik.tankschlau.util - otherwise we would overwrite production preferences.
    prefs = new UserPrefs("/de/fornalik/tankschlau/unittest");
  }

  @AfterEach
  void tearDownEach() throws BackingStoreException {
    // Delete "/unittest" node of persisted test-preferences
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

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void readAddress_returnsEmptyOptionalIfAddressPrefsDoNotExist(boolean removeNode)
  throws BackingStoreException {
    // given
    if (removeNode)
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

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void readGeo_returnsEmptyOptionalIfGeoLatLonDoNotExist(boolean removeNode)
  throws BackingStoreException {
    // given
    if (removeNode)
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

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void readPreferredPetrolType_returnsEmptyOptionalIfPrefDoesNotExist(boolean removeNode)
  throws BackingStoreException {
    // given
    if (removeNode)
      prefs.getRealPrefs().removeNode();

    // when
    Optional<PetrolType> actualPetrolType = prefs.readPreferredPetrolType();

    // then
    assertEquals(Optional.empty(), actualPetrolType);
  }

  @Test
  void writePushMessageUserId_writesProperly() {
    // given
    String givenUserId = "iahBzWeeC568g-pocEr-kkmM";

    // when
    prefs.writePushMessageUserId(givenUserId);

    // then
    Optional<String> actualUserId = prefs.readPushMessageUserId();

    assertTrue(actualUserId.isPresent());
    assertEquals(givenUserId, actualUserId.get());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void readPushMessageUserId_returnsEmptyOptionalIfPrefDoesNotExist(boolean removeNode)
  throws BackingStoreException {
    // given
    if (removeNode)
      prefs.getRealPrefs().removeNode();

    // when
    Optional<String> actualUserId = prefs.readPushMessageUserId();

    // then
    assertEquals(Optional.empty(), actualUserId);
  }

  @Test
  void writeApiKey_writesProperly() {
    // given
    String userPrefToken = "apikey.petrolstations";
    String givenApiKey = "1234-some-api-key-5678";

    // when
    prefs.writeApiKey(userPrefToken, givenApiKey);

    // then
    assertTrue(prefs.readApiKey(userPrefToken).isPresent());
    assertEquals(givenApiKey, prefs.readApiKey(userPrefToken).get());
  }

  @ParameterizedTest
  @ValueSource(booleans = {false, true})
  void readApiKey_returnsEmptyOptionalIfPrefDoesNotExist(boolean removeNode)
  throws BackingStoreException {
    // given
    if (removeNode)
      prefs.getRealPrefs().removeNode();

    // when
    Optional<String> actualApiKey = prefs.readApiKey("pref_does_not_exist");

    // then
    assertEquals(Optional.empty(), actualApiKey);
  }
}