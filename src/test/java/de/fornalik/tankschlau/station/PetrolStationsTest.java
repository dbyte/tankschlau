package de.fornalik.tankschlau.station;

import com.google.gson.JsonObject;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.helpers.response.FixtureFiles;
import de.fornalik.tankschlau.helpers.response.JsonResponseFixture;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

class PetrolStationsTest {

  @Test
  void sortByPriceForPetrolType_happy() {
    // given
    ArrayList<PetrolStation> givenPetrolStations, expectedPetrolStations;
    List<PetrolStation> actualPetrolStations;

    Pair<JsonResponseFixture, JsonObject> fixtures =
        JsonResponseFixture.createFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_30STATIONS_SORTEDBY_PRICE_FOR_DIESEL);

    JsonResponseFixture fixtureHelper = fixtures.getLeft();
    JsonObject jsonFixture = fixtures.getRight();

    // Convert from fixture-stations to a List of PetrolStations.
    expectedPetrolStations = new ArrayList<>();
    fixtureHelper.stations.forEach(
        (fixt) -> expectedPetrolStations.add(createFromStationDTO(fixt)));

    /* Scramble the order of Array elements, so we can test if it gets sorted as expected by
    the method under test. */
    givenPetrolStations = new ArrayList<>(expectedPetrolStations);
    Collections.shuffle(givenPetrolStations);

    // when
    actualPetrolStations = PetrolStations
        .sortByPriceForPetrolType(givenPetrolStations, PetrolType.DIESEL);

    // then
    Iterator<PetrolStation> iter = actualPetrolStations.listIterator();

    while (iter.hasNext()) {
      PetrolStation stationA = iter.next();
      if (!iter.hasNext()) break;
      PetrolStation stationB = iter.next();

      double priceA = stationA.getPetrols()
          .stream()
          .filter(p -> p.type == PetrolType.DIESEL)
          .findFirst()
          .map(p -> p.price)
          .orElse(9999.99);

      double priceB = stationB.getPetrols()
          .stream()
          .filter(p -> p.type == PetrolType.DIESEL)
          .findFirst()
          .map(p -> p.price)
          .orElse(9999.99);

      double distanceA = stationA.address.getGeo()
          .flatMap(Geo::getDistance)
          .orElse(9999.99);

      double distanceB = stationB.address.getGeo()
          .flatMap(Geo::getDistance)
          .orElse(9999.99);


      /* When there are 2 stations with same price and distance, we can't determine which of
      them comes first in the sort order, which is perfectly OK. */
      boolean isProperlySortedPrice = priceA <= priceB;
      boolean isLegalIndefinableSort = (priceA == priceB) && (distanceA == distanceB);

      if (!isProperlySortedPrice && !isLegalIndefinableSort) {
        printSortResult(actualPetrolStations);

        String failureMessage = String.format(
            "priceA: %f, distanceA: %f --- priceB: %f, distanceB: %f",
            priceA, distanceA, priceB, distanceB);

        Assertions.fail(failureMessage);
      }
    }
  }

  private PetrolStation createFromStationDTO(JsonResponseFixture.StationDTO dto) {
    Geo geo = new Geo(dto.lat, dto.lng, dto.distanceKm);

    Address address = new Address(
        dto.name,
        dto.street,
        dto.houseNumber,
        dto.city,
        dto.postCode,
        geo);

    Set<Petrol> petrols = new HashSet<>();
    petrols.add(new Petrol(PetrolType.DIESEL, dto.diesel));
    petrols.add(new Petrol(PetrolType.E10, dto.e10));
    petrols.add(new Petrol(PetrolType.E5, dto.e5));

    return PetrolStationBuilder
        .create(dto.uuid)
        .withBrand(dto.brand)
        .withIsOpen(dto.isOpen)
        .withAddress(address)
        .withPetrols(petrols)
        .build();
  }

  // Use for better orientation if sorting failed.
  private void printSortResult(List<PetrolStation> actualPetrolStations) {
    actualPetrolStations.forEach((elem) -> {
      System.out.print("PETROL > ");
      System.out.print(Petrols.findPetrol(elem.getPetrols(), PetrolType.DIESEL));
      System.out.print(" | DISTANCE > " + elem.address.getGeo().flatMap(Geo::getDistance));
      System.out.println();
    });
  }
}