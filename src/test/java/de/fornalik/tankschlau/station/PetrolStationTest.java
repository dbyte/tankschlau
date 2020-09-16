package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.helpers.mocks.PetrolStationMockHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PetrolStationTest {
  private static PetrolStationMockHelper mockHelp;

  @BeforeAll
  static void beforeAll() {
    PetrolStationTest.mockHelp = new PetrolStationMockHelper();
  }

  @AfterAll
  static void afterAll() {
    PetrolStationTest.mockHelp = null;
  }

  // region Constructor tests

  @Test
  void constructor_happy() {
    // given, when
    PetrolStation sut = new PetrolStation(
        mockHelp.uuidFixture,
        "brand",
        true,
        mockHelp.addressMock,
        mockHelp.petrolsFixture
    );

    // then
    assertEquals(mockHelp.uuidFixture, sut.uuid);
    assertEquals("brand", sut.brand);
    assertTrue(sut.isOpen);
    assertEquals(mockHelp.addressMock, sut.address);
    assertEquals(mockHelp.petrolsFixture, sut.getPetrols());
  }

  @Test
  void constructor_throwsNullPointerOnGivenNullArgument() {
    // given
    Runnable callWithNullUUID = () -> new PetrolStation(
        null,
        "brand",
        true,
        mockHelp.addressMock,
        mockHelp.petrolsFixture
    );

    Runnable callWithNullBrand = () -> new PetrolStation(
        mockHelp.uuidFixture,
        null,
        true,
        mockHelp.addressMock,
        mockHelp.petrolsFixture
    );

    Runnable callWithNullAddress = () -> new PetrolStation(
        mockHelp.uuidFixture,
        "brand",
        true,
        null,
        mockHelp.petrolsFixture
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
        mockHelp.uuidFixture,
        "brand",
        true,
        mockHelp.addressMock,
        null
    );

    // when, then
    assertDoesNotThrow(callWithNullPetrols::run);
  }

  // endregion

  @Test
  void getPetrols_happy() {
    // given
    PetrolStation petrolStation = PetrolStationBuilder
        .create(mockHelp.uuidFixture)
        .withBrand("Some Brand")
        .withIsOpen(true)
        .withAddress(mockHelp.addressMock)
        .withPetrols(mockHelp.petrolsFixture)
        .build();

    // when
    Set<Petrol> actualPetrols = petrolStation.getPetrols();

    // then
    assertEquals(2, actualPetrols.size());
  }

  @Test
  void getPetrols_returnsEmptyOptionalIfNoPetrolsAssigned() {
    // given
    PetrolStation petrolStation = PetrolStationBuilder
        .create(mockHelp.uuidFixture)
        .withBrand("Some Brand")
        .withIsOpen(true)
        .withAddress(mockHelp.addressMock)
        .build();

    // when
    Set<Petrol> actualPetrols = petrolStation.getPetrols();

    // then
    assertEquals(0, actualPetrols.size());
  }

  @Test
  void toString_doesNotThrow() {
    // given
    Set<Petrol> petrols = new HashSet<>();

    PetrolStation petrolStation = new PetrolStation(
        mockHelp.uuidFixture,
        "Some Brand",
        true,
        mockHelp.addressMock,
        petrols
    );

    // when then
    assertDoesNotThrow(petrolStation::toString);
  }
}