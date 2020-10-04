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

import com.google.gson.TypeAdapter;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.GeoRequest;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.StringResponse;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStationsJsonAdapter;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TankerkoenigPetrolStationsDaoTest {
  private static Geo geoMock;

  private TankerkoenigPetrolStationsDao sut;
  private List<PetrolStation> actualPetrolStations;

  private DomainFixtureHelp fixture;
  private HttpClient httpClientMock;
  private GeoRequest geoRequestMock;
  private StringResponse stringResponseMock;

  @BeforeAll
  static void beforeAll() {
    geoMock = Mockito.mock(Geo.class);
    when(geoMock.getDistance()).thenReturn(Optional.of(8.5));
    when(geoMock.getLatitude()).thenReturn(52.4079755);
    when(geoMock.getLongitude()).thenReturn(10.7725368);
  }

  @AfterAll
  static void afterAll() {
    geoMock = null;
  }

  @BeforeEach
  void setUp() {
    fixture = new DomainFixtureHelp();
    actualPetrolStations = null;

    TypeAdapter<List<PetrolStation>> petrolStationsJsonAdapter = new PetrolStationsJsonAdapter();

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

    when(stringResponseMock.getBody()).thenReturn(Optional.of(fixture.jsonFixture.toString()));
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
}