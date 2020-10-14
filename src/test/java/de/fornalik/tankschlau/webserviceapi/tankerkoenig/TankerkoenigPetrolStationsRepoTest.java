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
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.JsonResponse;
import de.fornalik.tankschlau.net.ResponseBody;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.station.PetrolsJsonAdapter;
import de.fornalik.tankschlau.testhelp_common.DomainFixtureHelp;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import de.fornalik.tankschlau.webserviceapi.common.GeoRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class TankerkoenigPetrolStationsRepoTest {
  private static Gson jsonProvider;
  private static Geo geoMock;

  private TankerkoenigPetrolStationsRepo sut;
  private List<PetrolStation> actualPetrolStations;

  private DomainFixtureHelp fixture;
  private HttpClient httpClientMock;
  private JsonResponse tankerkoenigResponseMock;
  private ResponseBody responseBodyMock;

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
    GeoRequest geoRequestMock = mock(GeoRequest.class);
    tankerkoenigResponseMock = mock(TankerkoenigResponse.class);
    responseBodyMock = mock(ResponseBody.class);

    sut = new TankerkoenigPetrolStationsRepo(
        httpClientMock,
        petrolStationsJsonAdapter,
        geoRequestMock,
        tankerkoenigResponseMock);
  }

  @Test
  void getAllInNeighbourhood_happy() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_17STATIONS_HAPPY);
    when(responseBodyMock.getData(String.class)).thenReturn(fixture.jsonFixture);

    when(tankerkoenigResponseMock.getBody()).thenReturn(responseBodyMock);
    when(httpClientMock.newCall(any(), any(), any())).thenReturn(tankerkoenigResponseMock);

    // when
    actualPetrolStations = sut.findAllInNeighbourhood(geoMock);

    // then
    fixture.assertEqualValuesIgnoringSort(actualPetrolStations);
  }

  @Test
  void getAllInNeighbourhood_returnsEmptyPetrolStationsArrayOnEmptyJsonResponse() {
    // given
    String jsonStringResponse = "{}";
    when(responseBodyMock.getData(String.class)).thenReturn(jsonStringResponse);

    when(tankerkoenigResponseMock.getBody()).thenReturn(responseBodyMock);
    when(httpClientMock.newCall(any(), any(), any())).thenReturn(tankerkoenigResponseMock);

    // when
    actualPetrolStations = sut.findAllInNeighbourhood(geoMock);

    // then
    assertEquals(0, actualPetrolStations.size());
  }
}