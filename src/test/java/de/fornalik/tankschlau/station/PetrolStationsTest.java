package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.testhelp_common.DomainFixtureHelp;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class PetrolStationsTest {
  PetrolStations.PriceAndDistanceComparator comparatorUnderTest;
  private DomainFixtureHelp fixture;
  private List<PetrolStation> givenPetrolStations;
  private List<PetrolStation> actualPetrolStations;

  @BeforeEach
  void setUp() {
    fixture = new DomainFixtureHelp();
    givenPetrolStations = null;
    actualPetrolStations = null;
    comparatorUnderTest = null;
  }

  @ParameterizedTest
  @EnumSource(PetrolType.class)
  void innerPriceAndDistanceComparator_compare_1stIsCheaperThan2nd(PetrolType givenPetrolType) {
    // given
    comparatorUnderTest = new PetrolStations.PriceAndDistanceComparator(givenPetrolType);

    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_COMPARE_TWO_1ST_IS_CHEAPEST);
    givenPetrolStations = fixture.convertToPetrolStations();
    PetrolStation expectedPetrolStation = givenPetrolStations.get(0);

    // when
    givenPetrolStations.sort(comparatorUnderTest);

    // then
    /* Assert that 1st station is cheaper than 2nd station for each given PetrolType
    (which is determined within the given fixtures). */
    assertEquals(expectedPetrolStation, givenPetrolStations.get(0));
  }

  @ParameterizedTest
  @EnumSource(PetrolType.class)
  void innerPriceAndDistanceComparator_compare_2ndIsCheaperAndFurtherThan1st(PetrolType givenPetrolType) {
    // given
    comparatorUnderTest = new PetrolStations.PriceAndDistanceComparator(givenPetrolType);

    fixture
        .setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_COMPARE_TWO_2ND_IS_CHEAPER_AND_FURTHER);
    givenPetrolStations = fixture.convertToPetrolStations();
    PetrolStation expectedPetrolStation = givenPetrolStations.get(1);

    // when
    givenPetrolStations.sort(comparatorUnderTest);

    // then
    /* Assert that 2nd station is cheaper and further away than 1st station for each given
    PetrolType (which is determined within the given fixtures).
    By business rules that does NOT matter yet and the second station must win. */
    assertEquals(expectedPetrolStation, givenPetrolStations.get(0));
  }

  @ParameterizedTest
  @EnumSource(PetrolType.class)
  void innerPriceAndDistanceComparator_compare_bothStationsHaveEqualPricesBut2ndIsCloser(
      PetrolType givenPetrolType) {
    // given
    comparatorUnderTest = new PetrolStations.PriceAndDistanceComparator(givenPetrolType);

    fixture
        .setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_COMPARE_TWO_EQUAL_PRICES_BUT_2ND_IS_CLOSER);
    givenPetrolStations = fixture.convertToPetrolStations();
    PetrolStation expectedPetrolStation = givenPetrolStations.get(1);

    // when
    givenPetrolStations.sort(comparatorUnderTest);

    // then
    /* Assert that both stations have equal prices for each given PetrolType (which is
    determined within the given fixtures), but 2nd station is closer than 1st.
    By business rules, the closer station must win - that is the 2nd. */
    assertEquals(expectedPetrolStation, givenPetrolStations.get(0));
  }

  @ParameterizedTest
  @EnumSource(PetrolType.class)
  void sortByPriceAndDistanceForPetrolType_happy(PetrolType givenPetrolType) {
    // given
    fixture
        .setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_34STATIONS_HAPPY);

    // Convert from fixture-stations to a List of real PetrolStation objects.
    givenPetrolStations = fixture.convertToPetrolStations();

    /* Scramble the order of Array elements, so we can test if it gets sorted as expected by
    the method under test. */
    Collections.shuffle(givenPetrolStations);

    // when
    PetrolStations.sortByPriceAndDistanceForPetrolType(givenPetrolStations, givenPetrolType);

    // then
    Iterator<PetrolStation> iter = givenPetrolStations.listIterator();

    while (iter.hasNext()) {
      PetrolStation stationA = iter.next();
      if (!iter.hasNext()) break;
      PetrolStation stationB = iter.next();

      double priceA = this.helpGetPriceForSort(stationA, givenPetrolType);
      double priceB = this.helpGetPriceForSort(stationB, givenPetrolType);

      double distanceA = helpGetDistanceForSort(stationA);
      double distanceB = helpGetDistanceForSort(stationB);

      // Expect that priceB is greater than or equal to priceA ...
      boolean isProperlySorted = priceA <= priceB;

      // ... or expect that there are 2 stations with same price and distance, where we can't
      // determine which of them comes first in the sort order.
      boolean isLegalIndefinableSorted = (priceA == priceB) && (distanceA == distanceB);
      if (isProperlySorted || isLegalIndefinableSorted)
        continue;

      // Fail case
      this.helpPrintSortResult(actualPetrolStations, givenPetrolType);

      String failureMessage = String.format(
          "TEST FAILED!\n"
              + "stationA (uuid %s) is not properly sorted to stationB (uuid %s).\n"
              + "priceA: %f, distanceA: %f compared to next priceB: %f, distanceB: %f "
              + "is a wrong sort order.",
          stationA.getUuid(), stationB.getUuid(), priceA, distanceA, priceB, distanceB);

      fail(failureMessage);
    }
  }

  private double helpGetPriceForSort(PetrolStation forPetrolStation, PetrolType forPetrolType) {
    return forPetrolStation
        .getPetrols()
        .stream()
        .filter(p -> p.type == forPetrolType)
        .findFirst()
        .map(p -> p.price)
        .orElse(9999.99);
  }

  private double helpGetDistanceForSort(PetrolStation forPetrolStation) {
    return forPetrolStation.getAddress()
        .getGeo()
        .flatMap(Geo::getDistance)
        .orElse(9999.99);
  }

  // Use for better orientation if sorting failed.
  private void helpPrintSortResult(
      List<PetrolStation> actualPetrolStations,
      PetrolType forPetrolType) {

    actualPetrolStations.forEach((elem) -> {
      System.out.println("Sorted for petrol type " + forPetrolType + ":");
      System.out.print("PETROL > ");
      System.out.print(Petrols.findPetrol(elem.getPetrols(), forPetrolType));
      System.out.print(" | DISTANCE > " + elem.getAddress().getGeo().flatMap(Geo::getDistance));
      System.out.println();
    });
  }
}