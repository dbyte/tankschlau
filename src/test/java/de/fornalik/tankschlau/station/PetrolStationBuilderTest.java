package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.helpers.mocks.PetrolStationMockHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class PetrolStationBuilderTest {
  private static PetrolStationMockHelper mockHelp;

  @BeforeAll
  static void beforeAll() {
    PetrolStationBuilderTest.mockHelp = new PetrolStationMockHelper();
  }

  @AfterAll
  static void afterAll() {
    PetrolStationBuilderTest.mockHelp = null;
  }

  @Test
  void create_happy() {
    // given
    UUID expectedUUID = mockHelp.uuidFixture;

    // when
    PetrolStationBuilder sut = this.helpCreateMinimumBuilder();
    PetrolStation actualStation = sut.build();

    // when, then
    assertEquals(expectedUUID, actualStation.uuid);
  }

  @Test
  void create_throwsNullPointerExceptionOnNullUUID() {
    // given
    Runnable callWithNullUUID = () -> PetrolStationBuilder.create(null);

    // when, then
    assertThrows(NullPointerException.class,
                 callWithNullUUID::run);
  }

  @Test
  void build_happy() {
    // given
    PetrolStation expectedPetrolStation = new PetrolStation(
        mockHelp.uuidFixture,
        "Some Brand",
        true,
        mockHelp.addressMock,
        mockHelp.petrolsFixture);

    PetrolStationBuilder sut = PetrolStationBuilder
        .create(expectedPetrolStation.uuid)
        .withBrand(expectedPetrolStation.brand)
        .withAddress(expectedPetrolStation.address)
        .withIsOpen(expectedPetrolStation.isOpen)
        .withPetrols(expectedPetrolStation.getPetrols());

    // when
    PetrolStation actualPetrolStation = sut.build();

    // then
    assertEquals(expectedPetrolStation.uuid, actualPetrolStation.uuid);
    assertEquals(expectedPetrolStation.brand, actualPetrolStation.brand);
    assertEquals(expectedPetrolStation.address, actualPetrolStation.address);
    assertEquals(expectedPetrolStation.isOpen, actualPetrolStation.isOpen);
    assertEquals(expectedPetrolStation.getPetrols(), actualPetrolStation.getPetrols());
  }

  @Test
  void withBrand_doesNotThrowOnValidArgument() {
    // given
    PetrolStationBuilder sut = this.helpCreateMinimumBuilder();

    // when, then
    assertDoesNotThrow(() -> sut.withBrand("This is a valid brand"));
  }

  @Test
  void withBrand_throwsNullPointerExceptionOnNullArgument() {
    // given
    PetrolStationBuilder sut = PetrolStationBuilder.create(mockHelp.uuidFixture);
    Runnable callWithNull = () -> sut.withBrand(null);

    // when, then
    assertThrows(NullPointerException.class,
                 callWithNull::run);
  }

  @Test
  void withPetrols_doesNotThrowOnValidArgument() {
    // given
    PetrolStationBuilder sut = this.helpCreateMinimumBuilder();

    // when, then
    assertDoesNotThrow(() -> sut.withPetrols(mockHelp.petrolsFixture));
  }

  @Test
  void withPetrols_throwsNullPointerExceptionOnNullArgument() {
    // given
    PetrolStationBuilder sut = PetrolStationBuilder.create(mockHelp.uuidFixture);
    Runnable callWithNull = () -> sut.withPetrols(null);

    // when, then
    assertThrows(NullPointerException.class,
                 callWithNull::run);
  }

  @Test
  void withAddress_doesNotThrowOnValidArgument() {
    // given
    PetrolStationBuilder sut = this.helpCreateMinimumBuilder();

    // when, then
    assertDoesNotThrow(() -> sut.withAddress(mockHelp.addressMock));
  }

  @Test
  void withAddress_throwsNullPointerExceptionOnNullArgument() {
    // given
    PetrolStationBuilder sut = PetrolStationBuilder.create(mockHelp.uuidFixture);
    Runnable callWithNull = () -> sut.withAddress(null);

    // when, then
    assertThrows(NullPointerException.class,
                 callWithNull::run);
  }

  private PetrolStationBuilder helpCreateMinimumBuilder() {
    return PetrolStationBuilder
        .create(mockHelp.uuidFixture)
        .withBrand("")
        .withAddress(mockHelp.addressMock);
  }
}
