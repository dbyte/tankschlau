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

package de.fornalik.tankschlau.webserviceapi.tankerkoenig;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.fornalik.tankschlau.helpers.response.FixtureFiles;
import de.fornalik.tankschlau.helpers.response.JsonResponseHelp;
import de.fornalik.tankschlau.station.PetrolStationsJsonAdapter;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class TankerkoenigResponseDtoTest {
  private static Gson gson;
  private static PetrolStationsJsonAdapter petrolStationsJsonAdapter;

  private TankerkoenigResponseDto actualTankerkoenigResponseDto;
  private Pair<JsonResponseHelp, JsonObject> responseHelp;
  private JsonResponseHelp objectFixture;
  private JsonObject jsonFixture;

  @BeforeAll
  static void beforeAll() {
    petrolStationsJsonAdapter = new PetrolStationsJsonAdapter();
    gson = new Gson();
  }

  @AfterAll
  static void afterAll() {
    gson = null;
    petrolStationsJsonAdapter = null;
  }

  @BeforeEach
  void setUp() {
    this.actualTankerkoenigResponseDto = null;
    this.responseHelp = null;
    this.objectFixture = null;
    this.jsonFixture = null;
  }

  @Test
  void read_oneStation_happy() {
    // given
    setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    // when
    actualTankerkoenigResponseDto = gson.fromJson(
        jsonFixture,
        TankerkoenigResponseDto.class);

    // then
    assertNotNull(actualTankerkoenigResponseDto);
    objectFixture.assertEqualValues(actualTankerkoenigResponseDto);
  }

  private void setupFixture(String resourceName) {
    responseHelp = JsonResponseHelp.createFromJsonFile(resourceName);
    objectFixture = responseHelp.getLeft();
    jsonFixture = responseHelp.getRight();
  }
}