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

import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.util.MyToStringBuilder;

import java.util.*;

public class PetrolStation {
  private static final String MUST_NOT_BE_NULL = " must not be null";

  @SerializedName("id") public final UUID uuid;
  @SerializedName("brand") public final String brand;
  @SerializedName("isOpen") public final boolean isOpen;
  public final Address address;
  private final HashSet<Petrol> petrols;

  /**
   * Constructor
   *
   * @param uuid    UUID of the station, given by API response - mandatory - not null
   * @param brand   Brand - can be empty String - not null
   * @param isOpen  Flags if station was open at API request time - default false
   * @param address Address and its geographical data - mandatory
   * @param petrols The station's petrol data - nullable
   */
  public PetrolStation(
      UUID uuid,
      String brand,
      boolean isOpen,
      Address address,
      Set<Petrol> petrols) {

    this.uuid = Objects.requireNonNull(uuid, "uuid" + MUST_NOT_BE_NULL);
    this.brand = Objects.requireNonNull(brand, "brand" + MUST_NOT_BE_NULL);
    this.isOpen = isOpen;
    this.address = Objects.requireNonNull(address, "address" + MUST_NOT_BE_NULL);
    this.petrols = Optional.ofNullable((HashSet<Petrol>) petrols).orElse(new HashSet<>());
  }

  /**
   * Get a Set of {@link Petrol} objects for this petrol station.
   * It's guaranteed that every station has a unique Set of petrols or an empty Set<br>
   * Cardinality is defined as 0...infinit.
   *
   * @return A Set of {@link Petrol} objects, else an empty Set.
   */
  public HashSet<Petrol> getPetrols() {
    if (petrols == null) return new HashSet<>();
    return petrols;
  }

  /**
   * Finds a {@link Petrol} object within this PetrolStation, searching by its {@link PetrolType}.
   *
   * @param type The {@link PetrolType} to search for.
   * @return An Optional when a {@link Petrol} object was found, else an empty Optional.
   * @see Petrols#findPetrol(Set, PetrolType)
   */
  public Optional<Petrol> findPetrol(PetrolType type) {
    return Petrols.findPetrol(petrols, type);
  }

  @Override
  public String toString() {
    return new MyToStringBuilder(this)
        .append("uuid", uuid)
        .append("brand", brand)
        .append("isOpen", isOpen)
        .append("address", address)
        .append("petrols", petrols)
        .toString();
  }
}
