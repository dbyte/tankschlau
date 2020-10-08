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
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * Service class for dealing with data collections of a {@link PetrolStation}.
 */
public class PetrolStations {
  private final PetrolStationsDao dao;

  /**
   * Constructor
   *
   * @param dao Some {@link PetrolStationsDao} implementation.
   */
  public PetrolStations(PetrolStationsDao dao) {
    this.dao = Objects.requireNonNull(
        dao, PetrolStationsDao.class.getSimpleName() + " must not be null");
  }

  /**
   * Gets a list of petrol stations around the user's neighbourhood, whereby neighbourhood
   * Basically, it's a service wrapper for {@link PetrolStationsDao#getAllInNeighbourhood(Geo)}.
   *
   * @param geo {@link Geo} data wrapping the user's current location.
   * @throws IOException If something went wrong while contacting the backing store of
   *                     {@link PetrolStation}.
   */
  public List<PetrolStation> getAllInNeighbourhood(Geo geo) throws IOException {
    return dao.getAllInNeighbourhood(geo);
  }

  /**
   * @return Further information about the last DAO operation.
   * @see PetrolStationsDao.TransactionInfo
   */
  // TODO unit tests
  public PetrolStationsDao.TransactionInfo getTransactionInfo() {
    return dao.getTransactionInfo();
  }

  /**
   * Creates a copy of the incoming List of {@link PetrolStation}, sorts the copy by
   * price and distance and returns it.
   *
   * @param stations List of {@link PetrolStation} to sort.
   * @param type     The {@link PetrolType} for which to sort the petrol stations.
   * @return A shallow copy of stations, sorted by price, then distance.
   */
  public static List<PetrolStation> sortByPriceAndDistanceForPetrolType(
      List<PetrolStation> stations,
      PetrolType type) {

    class PriceAndDistanceComparator implements Comparator<PetrolStation> {

      @Override
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
