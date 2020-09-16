package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Address;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class PetrolStationTest {

  private static UUID uuidFixture;
  private static Address addressMock;
  private static Petrol petrolMock1, petrolMock2;
  private static Set<Petrol> petrolsFixture;

  @BeforeAll
  static void beforeAll() {
    PetrolStationTest.uuidFixture = UUID.fromString("fb48f03e-c16e-435d-a845-6e235612f88f");
    PetrolStationTest.addressMock = mock(Address.class);
    PetrolStationTest.petrolMock1 = mock(Petrol.class);
    PetrolStationTest.petrolMock2 = mock(Petrol.class);
    PetrolStationTest.petrolsFixture = new HashSet<>();
    petrolsFixture.add(petrolMock1);
    petrolsFixture.add(petrolMock1);
  }

  @AfterAll
  static void afterAll() {
    uuidFixture = null;
    addressMock = null;
    petrolMock1 = null;
    petrolMock2 = null;
  }

  // region Constructor tests

  @Test
  void constructor_happy() {
    // given, when
    PetrolStation sut = new PetrolStation(
        uuidFixture,
        "brand",
        true,
        addressMock,
        petrolsFixture
    );

    // then
    assertEquals(uuidFixture, sut.uuid);
    assertEquals("brand", sut.brand);
    assertTrue(sut.isOpen);
    assertEquals(addressMock, sut.address);
    assertEquals(petrolsFixture, sut.getPetrols());
  }

  @Test
  void constructor_throwsNullPointerOnGivenNullArgument() {
    // given
    Runnable callWithNullUUID = () -> new PetrolStation(
        null,
        "brand",
        true,
        addressMock,
        petrolsFixture
    );

    Runnable callWithNullBrand = () -> new PetrolStation(
        uuidFixture,
        null,
        true,
        addressMock,
        petrolsFixture
    );

    Runnable callWithNullAddress = () -> new PetrolStation(
        uuidFixture,
        "brand",
        true,
        null,
        petrolsFixture
    );

    // when, then
    assertThrows(NullPointerException.class,
                 callWithNullUUID::run);

    // when, then
    assertThrows(NullPointerException.class,
                 callWithNullBrand::run);

    // when, then
    assertThrows(NullPointerException.class,
                 callWithNullAddress::run);
  }

  @Test
  void constructor_doesNotThrowOnGivenNullPetrols() {
    Runnable callWithNullPetrols = () -> new PetrolStation(
        uuidFixture,
        "brand",
        true,
        addressMock,
        null
    );

    // when, then
    assertDoesNotThrow(callWithNullPetrols::run);
  }

  // endregion

  @Test
  void getPetrols_happy() {
    // given
    Set<Petrol> expectedPetrols = new HashSet<>();
    expectedPetrols.add(petrolMock1);
    expectedPetrols.add(petrolMock2);

    PetrolStation petrolStation = PetrolStationBuilder
        .create(uuidFixture)
        .withBrand("Some Brand")
        .withIsOpen(true)
        .withAddress(addressMock)
        .withPetrols(expectedPetrols)
        .build();

    // when
    Set<Petrol> actualPetrols = petrolStation.getPetrols();

    // then
    assertEquals(2, actualPetrols.size());
  }

  @Test
  void toString_doesNotThrow() {
    // given
    Set<Petrol> petrols = new HashSet<>();

    PetrolStation petrolStation = new PetrolStation(
        uuidFixture,
        "Some Brand",
        true,
        addressMock,
        petrols
    );

    // when then
    assertDoesNotThrow(petrolStation::toString);
  }
}