package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.station.testhelp.PetrolStationTestHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PetrolStationTest {
  private static PetrolStationTestHelper mockHelp;

  @BeforeAll
  static void beforeAll() {
    PetrolStationTest.mockHelp = new PetrolStationTestHelper();
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
    assertEquals(mockHelp.addressMock, sut.getAddress());
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
    PetrolStation petrolStation = new PetrolStation(
        mockHelp.uuidFixture,
        "Some Brand",
        true,
        mockHelp.addressMock,
        mockHelp.petrolsFixture
    );

    // when
    Set<Petrol> actualPetrols = petrolStation.getPetrols();

    // then
    assertEquals(mockHelp.petrolsFixture.size(), actualPetrols.size());
  }

  @Test
  void getPetrols_returnsEmptyOptionalIfNoPetrolsAssigned() {
    // given
    PetrolStation petrolStation = new PetrolStation(
        mockHelp.uuidFixture,
        "Some Brand",
        true,
        mockHelp.addressMock,
        null
    );

    // when
    Set<Petrol> actualPetrols = petrolStation.getPetrols();

    // then
    assertEquals(0, actualPetrols.size());
  }

  @Test
  void findPetrol_happy() {
    /* Note that findPetrol delegates to utility method Petrols.findPetrol which has several
    tests on it. So we just do one happy path here */

    // given
    PetrolStation petrolStation = new PetrolStation(
        mockHelp.uuidFixture,
        "Some Brand",
        true,
        mockHelp.addressMock,
        mockHelp.petrolsFixture
    );

    // when
    Optional<Petrol> actualPetrol = petrolStation.findPetrol(PetrolType.DIESEL);
    // then
    Assertions.assertTrue(actualPetrol.isPresent());

    // when
    actualPetrol = petrolStation.findPetrol(null);
    // then
    Assertions.assertFalse(actualPetrol.isPresent());
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