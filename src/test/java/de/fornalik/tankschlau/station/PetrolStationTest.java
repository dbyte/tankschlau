package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Address;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

class PetrolStationTest {

  private static UUID uuidFixture;
  private static Address addressMock;
  private static Petrol petrolMock1, petrolMock2;

  @BeforeAll
  static void beforeAll() {
    PetrolStationTest.uuidFixture= UUID.fromString("fb48f03e-c16e-435d-a845-6e235612f88f");
    PetrolStationTest.addressMock = mock(Address.class);
    PetrolStationTest.petrolMock1 = mock(Petrol.class);
    PetrolStationTest.petrolMock2 = mock(Petrol.class);
  }

  @AfterAll
  static void afterAll() {
    uuidFixture = null;
    addressMock = null;
    petrolMock1 = null;
    petrolMock2 = null;
  }

  /*
  Note: The constructor of the SUT is private, so we do not test it. Instead, tests can be
  found at PetrolStationBuilderTest.
   */

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