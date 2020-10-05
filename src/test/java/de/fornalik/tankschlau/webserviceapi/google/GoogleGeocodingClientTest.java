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

package de.fornalik.tankschlau.webserviceapi.google;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.AddressRequest;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.StringResponse;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import de.fornalik.tankschlau.testhelp_common.GeocodingFixtureHelp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("OptionalGetWithoutIsPresent")
class GoogleGeocodingClientTest {
  private static HttpClient httpClientMock;
  private static StringResponse stringResponseMock;
  private static AddressRequest addressRequestMock;

  private GoogleGeocodingClient geocodingClient;
  private Geo actualGeo;
  private GeocodingFixtureHelp fixture;
  private Address addressMock;

  // region -----  SETUP  -----

  @BeforeAll
  static void beforeAll() {
    httpClientMock = mock(HttpClient.class);
    stringResponseMock = mock(StringResponse.class);
    addressRequestMock = mock(AddressRequest.class);
  }

  @AfterAll
  static void afterAll() {
    httpClientMock = null;
    stringResponseMock = null;
    addressRequestMock = null;
  }

  @BeforeEach
  void setUp() {
    this.addressMock = mock(Address.class);
    this.geocodingClient = new GoogleGeocodingClient(httpClientMock, addressRequestMock);
    this.actualGeo = null;
    this.fixture = new GeocodingFixtureHelp();
  }

  private void setupFixture(String path) throws IOException {
    fixture.setupFixture(path);

    when(stringResponseMock.getBody())
        .thenReturn(Optional.of(fixture.jsonFixture.toString()));

    when(httpClientMock.newCall(addressRequestMock))
        .thenReturn(stringResponseMock);
  }

  // endregion

  @Test
  void getGeo_returnsProperData() throws IOException {
    // given
    setupFixture(FixtureFiles.GOOGLE_GEO_RESPONSE_50_1078234_8_5413809_Rooftop);

    // when
    actualGeo = geocodingClient.getGeo(addressMock).get();

    // then
    fixture.assertEqualValues(actualGeo);
  }

  @Test
  void getGeo_setsProperTransactionInfo() throws IOException {
    // given
    setupFixture(FixtureFiles.GOOGLE_GEO_RESPONSE_50_1078234_8_5413809_Rooftop);

    // when
    actualGeo = geocodingClient.getGeo(addressMock).get();

    // then
    fixture.assertEqualValues(geocodingClient, true);
    assertEquals("Geo data powered by Google.", geocodingClient.getLicenseString());
  }
}