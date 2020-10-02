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

package de.fornalik.tankschlau.station.testhelp;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.station.Petrol;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.mock;

public class PetrolStationTestHelper {

  public final UUID uuidFixture;
  public final Address addressMock;
  public final Petrol petrolMock1, petrolMock2;
  public final Set<Petrol> petrolsFixture;
  public PetrolStation petrolStationMock;

  public PetrolStationTestHelper() {
    this.uuidFixture = UUID.fromString("fb48f03e-c16e-435d-a845-6e235612f88f");

    this.addressMock = mock(Address.class);

    this.petrolMock1 = mock(Petrol.class);
    this.petrolMock2 = mock(Petrol.class);

    this.petrolsFixture = new HashSet<>(createPetrolsFixture());

    this.petrolStationMock = mock(PetrolStation.class);
  }

  private Set<Petrol> createPetrolsFixture() {
    Set<Petrol> petrolsFixture = new HashSet<>();
    double price = 0.0;

    for (PetrolType type : PetrolType.values()) {
      price += 1.11;
      petrolsFixture.add(new Petrol(type, price));
    }

    return petrolsFixture;
  }
}
