package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Geo;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for collections of {@link PetrolStation}
 */
public class PetrolStations {

  public static List<PetrolStation> sortByPriceForPetrolType(
      List<PetrolStation> stations,
      PetrolType type) {

    class PriceComparator implements Comparator<PetrolStation> {
      public int compare(PetrolStation stationA, PetrolStation stationB) {
        double priceA = stationA.findPetrol(type)
            .map(p -> p.price)
            .orElse(9999.99);

        double priceB = stationB.findPetrol(type)
            .map(p -> p.price)
            .orElse(9999.99);

        double distanceA = stationA.address.getGeo()
            .flatMap(Geo::getDistance)
            .orElse(9999.99);

        double distanceB = stationB.address.getGeo()
            .flatMap(Geo::getDistance)
            .orElse(9999.99);

        return new CompareToBuilder()
            .append(priceA, priceB)
            .append(distanceA, distanceB)
            .toComparison();
      }
    }

    List<PetrolStation> stationsCopy = new ArrayList<>(stations);
    stationsCopy.sort(new PriceComparator());

    stationsCopy.forEach((elem) -> {
      System.out.print(
          elem.getPetrols()
              .stream()
              .filter(c -> c.type == PetrolType.DIESEL)
              .findFirst()
              .toString());

      System.out.print(
          " Distance " +
          elem.address.getGeo()
              .flatMap(Geo::getDistance)
              .orElse(9999.99));

      System.out.println();
    });

    return stationsCopy;
  }
}
