package de.fornalik.tankschlau.station;

import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.testhelp_common.DomainFixtureHelp;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PetrolStationsTest {
  private static TypeAdapter<?> petrolStationsGsonAdapter;
  private DomainFixtureHelp fixture;
  private List<PetrolStation> actualPetrolStations;

  @BeforeAll
  static void beforeAll() {
    petrolStationsGsonAdapter = new PetrolStationsJsonAdapter();
  }

  @AfterAll
  static void afterAll() {
    petrolStationsGsonAdapter = null;
  }

  @BeforeEach
  void setUp() {
    fixture = new DomainFixtureHelp();
    actualPetrolStations = null;
  }

  /*
  The underlying implementation of this factory method is subject to the corresponding
  net and web service units, so we just do some basic test on its own code paths here.
  */

  @Test
  void getAllInNeighbourhood_callsDaoAndReturnsResultingPetrolStations()
  throws IOException {
    // given
    PetrolStationsDao daoMock = mock(PetrolStationsDao.class);
    Geo geoMock = mock(Geo.class);

    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_34STATIONS_HAPPY);
    when(daoMock.getAllInNeighbourhood(geoMock)).thenReturn(fixture.convertToPetrolStations());

    // when
    actualPetrolStations = PetrolStations.getAllInNeighbourhood(daoMock, geoMock);

    // then
    fixture.assertEqualValuesIgnoringSort(actualPetrolStations);
  }

  // region createFromJson Tests
  /*
  The underlying implementation of this factory method is subject to the corresponding
  adapter unit, so we just do some basic test on its own code paths here.
  */

  @Test
  void createFromJson_doesCreateAllPetrolStations() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_34STATIONS_HAPPY);

    // when
    actualPetrolStations = PetrolStations
        .createFromJson(fixture.jsonFixture, petrolStationsGsonAdapter);

    // then
    fixture.assertEqualValuesIgnoringSort(actualPetrolStations);
  }

  @Test
  void createFromJson_returnsEmptyArrayOnMissingJsonInput() {
    // when
    actualPetrolStations = PetrolStations
        .createFromJson(new JsonObject(), petrolStationsGsonAdapter);

    // then
    assertEquals(0, actualPetrolStations.size());
  }

  @Test
  void createFromJson_throwsOnNonMatchingAdapterInstanceArgument() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    // when, then
    assertThrows(
        IllegalArgumentException.class,
        () -> PetrolStations.createFromJson(fixture.jsonFixture, null));
  }

  // endregion

  @ParameterizedTest
  @EnumSource(PetrolType.class)
  void sortByPriceAndDistanceForPetrolType_happy(PetrolType givenPetrolType) {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_34STATIONS_HAPPY);

    // Convert from fixture-stations to a List of real PetrolStation objects.
    List<PetrolStation> givenPetrolStations = fixture.convertToPetrolStations();

    /* Scramble the order of Array elements, so we can test if it gets sorted as expected by
    the method under test. */
    Collections.shuffle(givenPetrolStations);

    // when
    actualPetrolStations = PetrolStations
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
    return forPetrolStation.address
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
      System.out.print(" | DISTANCE > " + elem.address.getGeo().flatMap(Geo::getDistance));
      System.out.println();
    });
  }
}