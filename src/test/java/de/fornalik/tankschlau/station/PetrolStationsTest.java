package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.helpers.response.FixtureFiles;
import de.fornalik.tankschlau.helpers.response.JsonResponseFixture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

class PetrolStationsTest {

  @ParameterizedTest
  @EnumSource(PetrolType.class)
  void sortByPriceAndDistanceForPetrolType_happy(PetrolType givenPetrolType) {
    // given
    JsonResponseFixture fixtureHelper = JsonResponseFixture
        .createFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_30STATIONS_HAPPY)
        .getLeft();

    // Convert from fixture-stations to a List of real PetrolStation objects.
    List<PetrolStation> givenPetrolStations = fixtureHelper.convertToPetrolStations();

    /* Scramble the order of Array elements, so we can test if it gets sorted as expected by
    the method under test. */
    Collections.shuffle(givenPetrolStations);

    // when
    List<PetrolStation> actualPetrolStations = PetrolStations
        .sortByPriceAndDistanceForPetrolType(givenPetrolStations, givenPetrolType);

    // then
    Iterator<PetrolStation> iter = actualPetrolStations.listIterator();

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
          stationA.uuid, stationB.uuid, priceA, distanceA, priceB, distanceB);

      Assertions.fail(failureMessage);
    }
  }

  private double helpGetPriceForSort(PetrolStation forPetrolStation, PetrolType forPetrolType) {
    return forPetrolStation.getPetrols()
        .stream()
        .filter(p -> p.type == forPetrolType)
        .findFirst()
        .map(p -> p.price)
        .orElse(9999.99);
  }

  private double helpGetDistanceForSort(PetrolStation forPetrolStation) {
    return forPetrolStation.address.getGeo()
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
      System.out.print(" | DISTANCE > " + elem.address.getGeo().flatMap(Geo::getDistance));
      System.out.println();
    });
  }
}