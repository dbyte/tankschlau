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
import de.fornalik.tankschlau.testhelp_common.DomainFixtureHelp;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PetrolsJsonAdapterTest {
  private static Gson gson;

  private PetrolsJsonAdapter petrolsJsonAdapter;
  private Set<Petrol> actualPetrols;
  private DomainFixtureHelp fixture;

  @BeforeEach
  void setUp() {
    gson = new GsonBuilder()
        .registerTypeAdapter(Petrols.class, new PetrolsJsonAdapter())
        .create();

    petrolsJsonAdapter = new PetrolsJsonAdapter();
    actualPetrols = null;
    fixture = new DomainFixtureHelp();
  }

  @Test
  void read_happy() {
    // given
    fixture
        .setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    // when
    actualPetrols = gson.fromJson(fixture.jsonFixture, (Type) Petrols.class);

    // then
    fixture.assertEqualValuesIgnoringSort(actualPetrols, 0);
  }

  @Test
  void read_doesNotCreatePetrolsForMissingPrices() {
    // given
    fixture
        .setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_DIESEL_AND_E5);

    // when
    actualPetrols = gson.fromJson(fixture.jsonFixture, (Type) Petrols.class);

    // then
    // Expect that only 1 Petrol was created, because 2 of them miss their price in JSON.
    assertEquals(1, actualPetrols.size());
  }

  @Test
  void read_doesNotCreatePetrolsWithZeroPrice() {
    // given
    fixture
        .setupSingleFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_ZERO_PRICE_DIESEL_AND_E10);

    // when
    actualPetrols = gson.fromJson(fixture.jsonFixture, (Type) Petrols.class);

    // then
    // Expect that only 1 Petrol was created, because 2 of them have a 0.0 price JSON.
    assertEquals(1, actualPetrols.size());
  }

  @Test
  void write_throwsUnsupportedOperation() {
    // given
    Set<Petrol> emptyHashSet = new HashSet<>();

    // when then
    assertThrows(
        UnsupportedOperationException.class,
        () -> petrolsJsonAdapter.write(null, emptyHashSet));
  }
}