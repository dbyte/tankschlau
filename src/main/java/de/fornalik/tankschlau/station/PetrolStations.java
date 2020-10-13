/*
 * Copyright (c) 2020 Tammo Fornalik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.storage.PetrolStationsRepo;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Service class for dealing with data collections of a {@link PetrolStation}.
 */
public class PetrolStations {
  private final PetrolStationsRepo petrolStationsRepo;

  /**
   * Constructor
   *
   * @param petrolStationsWebRepo Some {@link PetrolStationsRepo} implementation.
   */
  public PetrolStations(PetrolStationsRepo petrolStationsWebRepo) {
    this.petrolStationsRepo = Objects.requireNonNull(
        petrolStationsWebRepo,
        PetrolStationsRepo.class.getSimpleName() + " must not be null");
  }

  /**
   * Sorts a {@code List} of {@link PetrolStation} first by price and then by distance.
   *
   * @param petrolStations List of {@link PetrolStation} to sort.
   * @param type           The {@link PetrolType} on which to sort the stations.
   * @see PriceAndDistanceComparator
   */
  public static void sortByPriceAndDistanceForPetrolType(
      List<PetrolStation> petrolStations,
      PetrolType type) {

    petrolStations.sort(new PriceAndDistanceComparator(type));
  }

  /**
   * Gets a list of petrol stations around the user's neighbourhood, whereby neighbourhood
   * Basically, it's a service wrapper for
   * {@link PetrolStationsRepo#getNeighbourhoodStations(Geo)} (Geo)}.
   *
   * @param geo {@link Geo} data wrapping the user's current location.
   */
  public List<PetrolStation> getAllInNeighbourhood(Geo geo) {
    return petrolStationsRepo.getNeighbourhoodStations(geo);
  }

  /**
   * Compares two {@link PetrolStation} objects, first by price and then by distance.
   * <br><br>
   * If the price of the given {@link PetrolType} is missing, its corresponding station is
   * considered greater then the other.
   * <br><br>
   * Same applies for missing distance within the address of the station.
   */
  public static class PriceAndDistanceComparator implements Comparator<PetrolStation> {
    private final PetrolType petrolType;

    public PriceAndDistanceComparator(PetrolType forPetrolType) {
      this.petrolType = forPetrolType;
    }

    @Override
    public int compare(PetrolStation first, PetrolStation second) {
      // sort empty Optionals last
      return new CompareToBuilder()
          .append(getPriceForSort(first), getPriceForSort(second))
          .append(getDistanceForSort(first), getDistanceForSort(second))
          .toComparison();
    }

    private Double getPriceForSort(PetrolStation station) {
      return station
          .findPetrol(this.petrolType)
          .map((p) -> p.price)
          .orElse(999999D);
    }

    private Double getDistanceForSort(PetrolStation station) {
      return station.address
          .getGeo()
          .flatMap(Geo::getDistance)
          .orElse(999999D);
    }
  }
}
