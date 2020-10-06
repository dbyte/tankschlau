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
import com.google.gson.GsonBuilder;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.station.PetrolsJsonAdapter;
import de.fornalik.tankschlau.testhelp_common.DomainFixtureHelp;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import de.fornalik.tankschlau.util.StringLegalizer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TankerkoenigJsonAdapterTest {
  private static Gson jsonProvider;

  private TankerkoenigJsonAdapter sut;
  private List<PetrolStation> actualPetrolStations;
  private DomainFixtureHelp fixture;

  @BeforeAll
  static void beforeAll() {
    jsonProvider = new GsonBuilder()
        .registerTypeAdapter(Petrols.class, new PetrolsJsonAdapter())
        .create();
  }

  @AfterAll
  static void afterAll() {
    jsonProvider = null;
  }

  @BeforeEach
  void setUp() {
    sut = new TankerkoenigJsonAdapter(jsonProvider);
    actualPetrolStations = null;
    fixture = new DomainFixtureHelp();
  }

  @Test
  void constructor_XXX() {

  }

  @Test
  void createPetrolStations_oneStation_happy() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    // when
    actualPetrolStations = sut.createPetrolStations(fixture.jsonFixture);

    // then
    assertNotNull(actualPetrolStations);
    assertEquals(1, actualPetrolStations.size());
    fixture.assertEqualValuesIgnoringSort(actualPetrolStations);
  }

  @Test
  void createPetrolStations_oneStation_returnsEmptyArrayIfJsonArrayElementsAreNoJsonObjects() {
    // given
    FileReader reader = FixtureFiles.getFileReaderForResource(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_STATIONS_ARRAY_IS_STRING_ARRAY);

    // when
    actualPetrolStations = sut.createPetrolStations(null);

    // then
    // Expect an empty PetrolStation array.
    assertEquals(0, actualPetrolStations.size());
  }

  @Test
  void createPetrolStations_oneStation_throwsOnMissingIdElement() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_MISSING_ID_ELEM);

    // when then
    Exception e = assertThrows(
        NullPointerException.class,
        () -> sut.createPetrolStations(fixture.jsonFixture));

    assertEquals("uuid must not be null", e.getMessage());
  }

  @Test
  void createFromJson_doesNotCreatePetrolsForMissingPrices() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_DIESEL_AND_E5);

    // when
    actualPetrolStations = sut.createPetrolStations(fixture.jsonFixture);

    // then
    // Expect that only 1 Petrol was created, because 2 of them miss their price in JSON.
    assertEquals(1, actualPetrolStations.get(0).getPetrols().size());
  }

  @Test
  void createFromJson_doesNotCreatePetrolsWithZeroPrice() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_ZERO_PRICE_DIESEL_AND_E10);

    // when
    actualPetrolStations = sut.createPetrolStations(fixture.jsonFixture);

    // then
    // Expect that only 1 Petrol was created, because 2 of them have a 0.0 price JSON.
    assertEquals(1, actualPetrolStations.get(0).getPetrols().size());
  }

  @Test
  void createPetrolStations_oneStation_acceptsEmptyBrand() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_HOUSENUM_AND_BRAND);

    // when
    actualPetrolStations = sut.createPetrolStations(fixture.jsonFixture);

    // then
    assertEquals("", actualPetrolStations.get(0).brand);
  }

  @Test
  void createPetrolStations_noStations_returnsEmptyArrayOnMissingStationsElement() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_STATIONS_ELEM);

    // when
    actualPetrolStations = sut.createPetrolStations(fixture.jsonFixture);

    // then
    assertEquals(0, actualPetrolStations.size());
  }

  @Test
  void createPetrolStations_emptyStations_returnsEmptyArrayOnEmptyStationsJsonArray() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_STATION_ARRAY);

    // when
    actualPetrolStations = sut.createPetrolStations(fixture.jsonFixture);

    // then
    assertEquals(0, actualPetrolStations.size());
  }

  @Test
  void createPetrolStations_addressAcceptsEmptyHouseNumber() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_HOUSENUM_AND_BRAND);

    // when
    actualPetrolStations = sut.createPetrolStations(fixture.jsonFixture);

    // then
    assertNotNull(actualPetrolStations.get(0).address);
    assertEquals("", actualPetrolStations.get(0).address.getHouseNumber());
  }

  @Test
  void createPetrolStations_throwsValueExceptionIfMandatoryAddressDataAreMissing() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_STREET_AND_PLACE_AND_POSTCODE);

    // when then
    assertThrows(
        StringLegalizer.ValueException.class,
        () -> sut.createPetrolStations(fixture.jsonFixture));
  }

  @Test
  void createPetrolStations_returnsEmptyOptionalGeoObjectIfAllGeoElementsAreMissing() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_ALL_GEO_ELEM);

    // when
    actualPetrolStations = sut.createPetrolStations(fixture.jsonFixture);

    // then
    assertNotNull(actualPetrolStations);
    assertEquals(1, actualPetrolStations.size());
    assertNotNull(actualPetrolStations.get(0).address);
    assertEquals(Optional.empty(), actualPetrolStations.get(0).address.getGeo());
  }

  @Test
  void createPetrolStations_doesHandleMissingGeoDistanceElement() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_DIST_ELEM);

    // when
    actualPetrolStations = sut.createPetrolStations(fixture.jsonFixture);

    // then
    assertNotNull(actualPetrolStations.get(0).address);
    assertTrue(actualPetrolStations.get(0).address.getGeo().isPresent());
    assertEquals(
        Optional.empty(),
        actualPetrolStations.get(0).address.getGeo().get().getDistance());
  }

  @Test
  void createPetrolStations_setsGeoLatLonToZeroOnMissingGeoLatLonElements() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_LAT_LON_ELEM);

    // when
    actualPetrolStations = sut.createPetrolStations(fixture.jsonFixture);

    // then
    assertTrue(actualPetrolStations.get(0).address.getGeo().isPresent());
    assertEquals(0.0, actualPetrolStations.get(0).address.getGeo().get().getLatitude());
    assertEquals(0.0, actualPetrolStations.get(0).address.getGeo().get().getLongitude());
  }

  @Test
  void createPetrolStations_multipleStations_happy() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_17STATIONS_HAPPY);

    // when
    actualPetrolStations = sut.createPetrolStations(fixture.jsonFixture);

    // then
    assertNotNull(actualPetrolStations);
    assertEquals(17, actualPetrolStations.size());
    fixture.assertEqualValuesIgnoringSort(actualPetrolStations);
  }
}
