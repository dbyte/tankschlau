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
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.GeoRequest;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.StringResponse;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.station.PetrolsJsonAdapter;
import de.fornalik.tankschlau.testhelp_common.DomainFixtureHelp;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TankerkoenigPetrolStationsDaoTest {
  private static Gson jsonProvider;
  private static Geo geoMock;

  private TankerkoenigPetrolStationsDao sut;
  private List<PetrolStation> actualPetrolStations;

  private DomainFixtureHelp fixture;
  private HttpClient httpClientMock;
  private GeoRequest geoRequestMock;
  private StringResponse stringResponseMock;

  @BeforeAll
  static void beforeAll() {
    jsonProvider = new GsonBuilder()
        .registerTypeAdapter(Petrols.class, new PetrolsJsonAdapter())
        .create();

    geoMock = Mockito.mock(Geo.class);
    when(geoMock.getDistance()).thenReturn(Optional.of(8.5));
    when(geoMock.getLatitude()).thenReturn(52.4079755);
    when(geoMock.getLongitude()).thenReturn(10.7725368);
  }

  @AfterAll
  static void afterAll() {
    jsonProvider = null;
    geoMock = null;
  }

  @BeforeEach
  void setUp() {
    fixture = new DomainFixtureHelp();
    actualPetrolStations = null;

    TankerkoenigJsonAdapter petrolStationsJsonAdapter = new TankerkoenigJsonAdapter(jsonProvider);

    httpClientMock = mock(HttpClient.class);
    geoRequestMock = mock(GeoRequest.class);
    stringResponseMock = mock(StringResponse.class);

    sut = new TankerkoenigPetrolStationsDao(
        httpClientMock,
        petrolStationsJsonAdapter,
        geoRequestMock);
  }

  @Test
  void getAllInNeighbourhood_happy() throws IOException {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_17STATIONS_HAPPY);

    when(stringResponseMock.getBody()).thenReturn(Optional.of(fixture.jsonFixture));
    when(httpClientMock.newCall(geoRequestMock)).thenReturn(stringResponseMock);

    // when
    actualPetrolStations = sut.getAllInNeighbourhood(geoMock);

    // then
    fixture.assertEqualValues(sut);
    fixture.assertEqualValuesIgnoringSort(actualPetrolStations);
  }

  @Test
  void getAllInNeighbourhood_returnsEmptyPetrolStationsArrayOnEmptyJsonResponse()
  throws IOException {
    // given
    String jsonStringResponse = "{}";

    when(stringResponseMock.getBody()).thenReturn(Optional.of(jsonStringResponse));
    when(httpClientMock.newCall(geoRequestMock)).thenReturn(stringResponseMock);

    // when
    actualPetrolStations = sut.getAllInNeighbourhood(geoMock);

    // then
    assertEquals(0, actualPetrolStations.size());
  }

  @Test
  void getAllInNeighbourhood_setsTransactionInfoProperlyIfResponseIsNull() throws IOException {
    // given
    when(httpClientMock.newCall(geoRequestMock)).thenReturn(null);

    // when
    actualPetrolStations = sut.getAllInNeighbourhood(geoMock);

    // then
    assertEquals(0, actualPetrolStations.size());
    assertFalse(sut.getTransactionInfo().isOk());
    assertEquals(
        TankerkoenigPetrolStationsDao.class.getSimpleName() + "_RESPONSE_NULL",
        sut.getTransactionInfo().getStatus());
    assertEquals("Response is null.", sut.getTransactionInfo().getMessage());
  }

  @Test
  void getAllInNeighbourhood_setsTransactionInfoProperlyIfBodyIsEmpty() throws IOException {
    // given
    when(stringResponseMock.getBody()).thenReturn(Optional.empty());
    when(httpClientMock.newCall(geoRequestMock)).thenReturn(stringResponseMock);

    // when
    actualPetrolStations = sut.getAllInNeighbourhood(geoMock);

    // then
    assertEquals(0, actualPetrolStations.size());

    assertFalse(sut.getTransactionInfo().isOk());

    assertEquals(
        TankerkoenigPetrolStationsDao.class.getSimpleName() + "_EMPTY_RESPONSE_BODY",
        sut.getTransactionInfo().getStatus());

    assertEquals("Response body is empty.", sut.getTransactionInfo().getMessage());
  }

  @Test
  void getAllInNeighbourhood_setsTransactionInfoProperlyIfOkIsFalse() throws IOException {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_MISSING_APIKEY);

    when(stringResponseMock.getBody()).thenReturn(Optional.of(fixture.jsonFixture));
    when(httpClientMock.newCall(geoRequestMock)).thenReturn(stringResponseMock);

    // when
    actualPetrolStations = sut.getAllInNeighbourhood(geoMock);

    // then
    assertEquals(0, actualPetrolStations.size());

    assertFalse(sut.getTransactionInfo().isOk());

    assertEquals(
        "error",
        sut.getTransactionInfo().getStatus());

    assertEquals(
        "apikey nicht angegeben, falsch, oder im falschen Format",
        sut.getTransactionInfo().getMessage());
  }

  @Test
  void getAllInNeighbourhood_setsTransactionInfoProperlyIfApiRespondsWithError()
  throws IOException {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_LONGITUDE_ERROR);

    when(stringResponseMock.getBody()).thenReturn(Optional.of(fixture.jsonFixture));
    when(httpClientMock.newCall(geoRequestMock)).thenReturn(stringResponseMock);

    // when
    actualPetrolStations = sut.getAllInNeighbourhood(geoMock);

    // then
    assertEquals(0, actualPetrolStations.size());

    assertFalse(sut.getTransactionInfo().isOk());

    assertEquals(
        "error",
        sut.getTransactionInfo().getStatus());

    assertEquals(
        "lng nicht angegeben, oder ausserhalb der gültigen Grenzen",
        sut.getTransactionInfo().getMessage());
  }
}