package de.fornalik.tankschlau.util;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;

import java.util.Optional;
import java.util.prefs.BackingStoreException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for UserPreferences. We execute combined write/read tests.
 */
class UserPreferencesTest {
  private static Address addressMock;
  private static Geo geoMock;
  private UserPreferences prefs;

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
  void setUp() throws BackingStoreException {
    Class<?> thisClazz = this.getClass();

    // Clear the node of possible persisted preferences ...
    new UserPreferences(thisClazz).getJavaPref().removeNode();

    // ... and start with a fresh one.
    prefs = new UserPreferences(thisClazz);
  }

  @Test
  void writeUserAddress_writesProperly() {
    // when
    prefs.writeUserAddress(addressMock);
    Address actualAddress = prefs.readUserAddress();

    // then
    assertEquals(addressMock.getName(), actualAddress.getName());
    assertEquals(addressMock.getCity(), actualAddress.getCity());
    assertEquals(addressMock.getPostCode(), actualAddress.getPostCode());
    assertEquals(addressMock.getStreet(), actualAddress.getStreet());
    assertEquals(addressMock.getHouseNumber(), actualAddress.getHouseNumber());

    assertTrue(actualAddress.getGeo().isPresent());
    Geo actualGeo = actualAddress.getGeo().get();
    assert addressMock.getGeo().isPresent();

    assertEquals(addressMock.getGeo().get().getLatitude(), actualGeo.getLatitude());
    assertEquals(addressMock.getGeo().get().getLongitude(), actualGeo.getLongitude());
    assertEquals(addressMock.getGeo().get().getDistance(), actualGeo.getDistance());
  }

  @Test
    // TODO
  void writeUserGeo() {
  }

  @ParameterizedTest
  @EnumSource(PetrolType.class)
  void writePreferredPetrolType_writesProperly(PetrolType givenPetrolType) {
    // when
    prefs.writePreferredPetrolType(givenPetrolType);
    PetrolType actualPetrolType = prefs.readPreferredPetrolType();

    // then
    assertEquals(givenPetrolType, actualPetrolType);
  }
}