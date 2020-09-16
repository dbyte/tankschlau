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

  /**
   * Creates a copy of the incoming List of {@link PetrolStation}, sorts the copy by
   * price and distance and returns it.
   *
   * @param stations List of {@link PetrolStation} to sort.
   * @param type The {@link PetrolType} for which to sort the petrol stations.
   * @return A shallow copy of stations, sorted by price, then distance.
   */
  public static List<PetrolStation> sortByPriceAndDistanceForPetrolType(
      List<PetrolStation> stations,
      PetrolType type) {

    class PriceAndDistanceComparator implements Comparator<PetrolStation> {
      public int compare(PetrolStation stationA, PetrolStation stationB) {

        double priceA = getPriceForSort(stationA, type);
        double priceB = getPriceForSort(stationB, type);

        double distanceA = getDistanceForSort(stationA);
        double distanceB = getDistanceForSort(stationB);

        return new CompareToBuilder()
            .append(priceA, priceB)
            .append(distanceA, distanceB)
            .toComparison();
      }
    }

    List<PetrolStation> stationsCopy = new ArrayList<>(stations);
    stationsCopy.sort(new PriceAndDistanceComparator());

    return stationsCopy;
  }

  private static double getPriceForSort(PetrolStation station, PetrolType type) {
    return station.findPetrol(type)
        .map((petrol) -> petrol.price)
        .orElse(9999.99);
  }

  private static double getDistanceForSort(PetrolStation station) {
    return station.address.getGeo()
        .flatMap(Geo::getDistance)
        .orElse(9999.99);
  }
}
