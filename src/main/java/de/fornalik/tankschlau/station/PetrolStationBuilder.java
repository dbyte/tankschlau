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

import de.fornalik.tankschlau.geo.Address;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PetrolStationBuilder {
  private static final String MUST_NOT_BE_NULL = " must not be null";

  private final UUID uuid;
  private Set<Petrol> petrols = new HashSet<>();
  private String brand;
  private boolean isOpen;
  private Address address;

  private PetrolStationBuilder(UUID uuid) {
    this.uuid = Objects.requireNonNull(uuid, "uuid" + MUST_NOT_BE_NULL);
  }

  public static PetrolStationBuilder create(UUID uuid) {
    return new PetrolStationBuilder(uuid);
  }

  public PetrolStation build() {
    return new PetrolStation(uuid, brand, isOpen, address, petrols);
  }

  public PetrolStationBuilder withBrand(String brand) {
    this.brand = Objects.requireNonNull(brand, "brand" + MUST_NOT_BE_NULL);
    return this;
  }

  public PetrolStationBuilder withIsOpen(boolean isOpen) {
    this.isOpen = isOpen;
    return this;
  }

  public PetrolStationBuilder withPetrols(Set<Petrol> petrols) {
    this.petrols = Objects.requireNonNull((HashSet<Petrol>) petrols, "petrol" + MUST_NOT_BE_NULL);
    return this;
  }

  public PetrolStationBuilder withAddress(Address address) {
    this.address = Objects.requireNonNull(address, "address" + MUST_NOT_BE_NULL);
    return this;
  }
}
