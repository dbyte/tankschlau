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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.CompareToBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Utility class for collections of Petrol
 */
public class Petrols {

  private Petrols() {
  }

  /**
   * Creates a Set of petrols and their prices from a given {@link JsonObject}.
   *
   * @param in {@link JsonObject} from which to convert to a Set of Petrols.
   * @return {@link Petrol} Set or empty Set if no petrol prices exist in the passed JSON.
   */
  public static Set<Petrol> createFromJson(JsonObject in) {
    Set<Petrol> petrols = new HashSet<>();

    for (PetrolType petrolType : PetrolType.values()) {
      String jsonPetrolTypeKey = petrolType.getJsonKey();

      JsonElement price = in.get(jsonPetrolTypeKey);
      if (price == null || price.isJsonNull() || price.getAsDouble() <= 0.0) continue;

      petrols.add(new Petrol(petrolType, price.getAsDouble()));
    }

    return petrols;
  }

  /**
   * Use for sorting a {@link Petrol} Set, based on the order of values defined in enum
   * {@link PetrolType} and price. The array gets sorted based on the order in which the enum
   * values are defined.
   *
   * @param petrols List of {@link Petrol}s. Empty List if given petrols are null.
   */
  public static List<Petrol> getSortedByPetrolTypeAndPrice(Set<Petrol> petrols) {
    if (petrols == null) return new ArrayList<>();
    ArrayList<Petrol> petrolsList = new ArrayList<>(petrols);

    class ComparatorByOrdinalPetrolTypeAndPrice implements Comparator<Petrol> {
      public int compare(Petrol petrolA, Petrol petrolB) {
        return new CompareToBuilder()
            .append(petrolA.type.ordinal(), petrolB.type.ordinal())
            .append(petrolA.price, petrolB.price)
            .toComparison();
      }
    }

    petrolsList.sort(new ComparatorByOrdinalPetrolTypeAndPrice());
    return petrolsList;
  }

  /**
   * Finds a {@link Petrol} object for a {@link PetrolType} in a list of petrols.
   *
   * @param in    Set of {@link Petrol} to search in
   * @param petrolType Type of petrol we search for, assuming it's unique within the collection.
   * @return Optional {@link Petrol} object or Optional empty if not found.
   * @throws PetrolsDuplicateException If we found {@link PetrolType} duplicates in the list.
   */
  public static Optional<Petrol> findPetrol(Set<Petrol> in, PetrolType petrolType) {
    if (in == null || petrolType == null) return Optional.empty();

    List<Petrol> foundPetrols = in.stream()
                                  .filter(petrol -> petrol.type == petrolType)
                                  .collect(Collectors.toCollection(ArrayList::new));

    int countResults = foundPetrols.size();

    // As petrols is a Set here, this should never happen. Being defensive, we handle it anyway.
    if (countResults >= 2)
      throw new PetrolsDuplicateException(
          String.format("Critical error. Found %d duplicates for petrol type %s",
                        countResults,
                        petrolType));

    return countResults > 0 ? Optional.of(foundPetrols.get(0)) : Optional.empty();
  }

  /**
   * Thrown when we've found duplicates of {@link PetrolType} in a {@link Petrol} collection.
   *
   * @implNote This is an unchecked Exception.
   */
  public static class PetrolsDuplicateException extends RuntimeException {
    public PetrolsDuplicateException(String message) {
      super(message);
    }
  }
}
