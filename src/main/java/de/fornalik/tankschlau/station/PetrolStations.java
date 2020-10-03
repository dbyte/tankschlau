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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigPetrolStationsDao;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Utility class for collections of {@link PetrolStation}
 */
public class PetrolStations {

  public static List<PetrolStation> getAllInNeighbourhood(
      TankerkoenigPetrolStationsDao dao, Geo geo) throws IOException {

    return dao.getAllInNeighbourhood(geo);
  }

  /**
   * Creates a List of {@link PetrolStation} from a given JSON string.
   *
   * @param in          JSON string from which to convert.
   * @param gsonAdapter The (customized) Gson {@link TypeAdapter} adapter that will be used for
   *                    conversion (which should have been initialized once at application start).
   * @return A List of {@link PetrolStation}.
   * @throws IllegalArgumentException If gsonAdapter is not an instance of
   *                                  {@link PetrolStationsJsonAdapter}
   * @throws IllegalStateException    If Gson finds an invalid JSON object
   */
  public static List<PetrolStation> createFromJson(String in, TypeAdapter<?> gsonAdapter) {
    if (in == null)
      return new ArrayList<>();

    if (!(gsonAdapter instanceof PetrolStationsJsonAdapter))
      throw new IllegalArgumentException(
          "Argument must be an instance of " + PetrolStationsJsonAdapter.class);

    Gson gson = new GsonBuilder()
        .registerTypeAdapter(PetrolStation.class, gsonAdapter)
        .create();

    return gson.fromJson(in, (Type) PetrolStation.class);
  }

  /**
   * Creates a List of {@link PetrolStation} from a given {@link JsonObject}.
   *
   * @param in          {@link JsonObject} from which to convert.
   * @param gsonAdapter see {@link #createFromJson(String, TypeAdapter)}
   * @return see {@link #createFromJson(String, TypeAdapter)}
   * @see #createFromJson(String, TypeAdapter)
   */
  public static List<PetrolStation> createFromJson(JsonObject in, TypeAdapter<?> gsonAdapter) {
    return createFromJson(in.toString(), gsonAdapter);
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
