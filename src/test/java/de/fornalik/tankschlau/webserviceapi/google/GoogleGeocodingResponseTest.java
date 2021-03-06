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
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.ResponseBody;
import de.fornalik.tankschlau.service.TransactInfo;
import de.fornalik.tankschlau.service.TransactInfoImpl;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import de.fornalik.tankschlau.testhelp_common.GeocodingFixtureHelp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class GoogleGeocodingResponseTest {
  private static Gson jsonProvider;
  private Geo actualGeo;
  private GeocodingFixtureHelp fixture;
  private GoogleGeocodingResponse googleGeocodingResponse;

  @BeforeAll
  static void beforeAll() {
    jsonProvider = new Gson();
  }

  @AfterAll
  static void afterAll() {
    jsonProvider = null;
  }

  @BeforeEach
  void setUp() {
    ResponseBody responseBodyMock = mock(ResponseBody.class);

    // No mock - need real object here, tightly coupled
    TransactInfo transactInfoMock = new TransactInfoImpl();

    this.googleGeocodingResponse = new GoogleGeocodingResponse(
        jsonProvider,
        responseBodyMock,
        transactInfoMock); // SUT
    this.actualGeo = null;
    this.fixture = new GeocodingFixtureHelp();
  }

  @Test
  void construct_throwsImmediatelyOnNullArguments() {
    assertThrows(NullPointerException.class, () -> new GoogleGeocodingResponse(null, null, null));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      FixtureFiles.GOOGLE_GEO_RESPONSE_52_39097_10_84663_Rooftop,
      FixtureFiles.GOOGLE_GEO_RESPONSE_52_5006049_13_3136007_GeometricCenter,
      FixtureFiles.GOOGLE_GEO_RESPONSE_52_9541353_8_2396026_Approximate})
  void fromJson_deserializesGivenJsonToGeoObject_happy(String givenJsonString) {
    // given
    fixture.setupFixture(givenJsonString);

    // when
    actualGeo = googleGeocodingResponse.fromJson(fixture.jsonFixture, Geo.class).orElse(null);

    // then
    assertNotNull(actualGeo);
    fixture.assertEqualValues(actualGeo);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      FixtureFiles.GOOGLE_GEO_RESPONSE_52_39097_10_84663_Rooftop,
      FixtureFiles.GOOGLE_GEO_RESPONSE_52_5006049_13_3136007_GeometricCenter,
      FixtureFiles.GOOGLE_GEO_RESPONSE_52_9541353_8_2396026_Approximate,
      FixtureFiles.GOOGLE_GEO_RESPONSE_MissingApiKey,
      FixtureFiles.GOOGLE_GEO_RESPONSE_ZeroResults})
  void fromJson_setsTransactInfoAccordingToGoogleResponse_happy(String givenJsonString) {
    // given
    fixture.setupFixture(givenJsonString);

    // when
    googleGeocodingResponse.fromJson(fixture.jsonFixture, Geo.class);

    // then
    assertNotNull(googleGeocodingResponse);
    fixture.assertEqualValues(googleGeocodingResponse.getTransactInfo());
  }

  @ParameterizedTest
  @ValueSource(strings = {
      FixtureFiles.GOOGLE_GEO_RESPONSE_ZeroResults,
      FixtureFiles.GOOGLE_GEO_RESPONSE_MissingApiKey})
  void fromJson_returnsEmptyOptionalIfGoogleRespondedWithZeroResults(String givenJsonString) {
    // given
    fixture.setupFixture(givenJsonString);

    // when
    Optional<Geo> expectedGeo = googleGeocodingResponse.fromJson(fixture.jsonFixture, Geo.class);

    // then
    assertEquals(Optional.empty(), expectedGeo);
  }

  @Test
  void fromJson_returnsEmptyOptionalIfGivenJsonStringIsNullOrEmpty() {
    // when
    Optional<Geo> actualOptionalGeo = googleGeocodingResponse.fromJson(null, Geo.class);
    // then
    assertEquals(Optional.empty(), actualOptionalGeo);

    // when
    actualOptionalGeo = googleGeocodingResponse.fromJson("", Geo.class);
    // then
    assertEquals(Optional.empty(), actualOptionalGeo);
  }

  @Test
  void fromJson_setsCustomErrorMessageIfDeserializationResultIsNull() {
    // given
    String expectedMessagePart = "JSON string could not be converted";

    // when
    googleGeocodingResponse.fromJson(null, String.class);

    // then
    assertTrue(
        googleGeocodingResponse
            .getTransactInfo()
            .getErrorMessage()
            .orElse("")
            .contains(expectedMessagePart),
        "\nExpected error message to contain\n"
        + "\"" + expectedMessagePart + "\"\n"
        + "but actually is\n"
        + "\"" + googleGeocodingResponse.getTransactInfo().getErrorMessage() + "\"");
  }
}