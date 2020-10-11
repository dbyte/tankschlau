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

import com.google.gson.Gson;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import de.fornalik.tankschlau.testhelp_common.GeocodingFixtureHelp;
import de.fornalik.tankschlau.webserviceapi.common.AddressRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


class GoogleGeocodingClientTest {
  private static HttpClient httpClientMock;
  private static AddressRequest addressRequestMock;
  private static Gson jsonProvider;

  private GoogleGeocodingClient geocodingClient;
  private Geo actualGeo;
  private GeocodingFixtureHelp fixture;
  private Address addressMock;
  private GoogleGeocodingResponse response;

  // region -----  SETUP  -----

  @BeforeAll
  static void beforeAll() {
    httpClientMock = mock(HttpClient.class);
    addressRequestMock = mock(AddressRequest.class);
    jsonProvider = new Gson();
  }

  @AfterAll
  static void afterAll() {
    httpClientMock = null;
    addressRequestMock = null;
    jsonProvider = null;
  }

  @BeforeEach
  void setUp() {
    this.addressMock = mock(Address.class);
    this.actualGeo = null;
    this.fixture = new GeocodingFixtureHelp();

    // No mock here... must be a real object, sorry for that :-)
    response = new GoogleGeocodingResponse(jsonProvider);
  }

  private void setupFixture(String path) {
    fixture.setupFixture(path);

    response.setBody(fixture.jsonFixture);

    when(httpClientMock.newCall(any(), any())).thenReturn(response);

    this.geocodingClient = new GoogleGeocodingClient(
        httpClientMock,
        jsonProvider,
        addressRequestMock);

    /*when(response.getBody())
        .thenReturn(Optional.of(fixture.jsonFixture()));*/
  }

  // endregion

  @ParameterizedTest
  @ValueSource(strings = {
      FixtureFiles.GOOGLE_GEO_RESPONSE_50_1078234_8_5413809_Rooftop,
      FixtureFiles.GOOGLE_GEO_RESPONSE_52_39097_10_84663_Rooftop,
      FixtureFiles.GOOGLE_GEO_RESPONSE_52_5006049_13_3136007_GeometricCenter,
      FixtureFiles.GOOGLE_GEO_RESPONSE_52_9541353_8_2396026_Approximate,
  })
  void getGeo_returnsProperGeoInstanceOnHappyResponse(String fixturePath) {
    // given
    setupFixture(fixturePath);

    // when
    //noinspection OptionalGetWithoutIsPresent
    actualGeo = geocodingClient.getGeo(addressMock).get();

    // then
    fixture.assertEqualValues(actualGeo);
  }

  @Test
  void getGeo_shouldCrashWithNullPointerExceptionIfResponseIsNull() {
    // given
    response = null;
    when(httpClientMock.newCall(any(), any())).thenReturn(response);

    // when then
    assertThrows(NullPointerException.class, () -> geocodingClient.getGeo(addressMock));
  }

  @Test
  void getGeo_returnsEmptyGeoIfResponseBodyIsEmpty() {
    // given
    setupFixture(FixtureFiles.GOOGLE_GEO_RESPONSE_MissingApiKey); // does not matter here
    response.setBody(null);

    // when
    Optional<Geo> actualOptionalGeo = geocodingClient.getGeo(addressMock);

    // when then
    assertEquals(Optional.empty(), actualOptionalGeo);
  }

  @Test
  void getGeo_returnsExpectedLicenceInfo() {
    // given
    setupFixture(FixtureFiles.GOOGLE_GEO_RESPONSE_50_1078234_8_5413809_Rooftop);

    // when
    geocodingClient.getGeo(addressMock);

    // then
    assertEquals("Geo data powered by Google.", geocodingClient.getLicenseString());
  }
}