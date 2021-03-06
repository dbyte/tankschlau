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
import de.fornalik.tankschlau.net.ResponseBody;
import de.fornalik.tankschlau.net.ResponseBodyImpl;
import de.fornalik.tankschlau.service.TransactInfo;
import de.fornalik.tankschlau.service.TransactInfoImpl;
import de.fornalik.tankschlau.testhelp_common.DomainFixtureHelp;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TankerkoenigResponseTest {
  private static Gson jsonProvider;
  private DomainFixtureHelp fixture;
  private TankerkoenigResponse tankerkoenigResponse;

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
    this.fixture = new DomainFixtureHelp();

    // No mock as these are tightly coupled with response.
    ResponseBody responseBody = new ResponseBodyImpl();
    TransactInfo transactInfo = new TransactInfoImpl();

    // SUT
    this.tankerkoenigResponse = new TankerkoenigResponse(
        jsonProvider,
        responseBody,
        transactInfo);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_17STATIONS_HAPPY,
      FixtureFiles.TANKERKOENIG_JSON_RESPONSE_MISSING_APIKEY,
      FixtureFiles.TANKERKOENIG_JSON_RESPONSE_LONGITUDE_ERROR,
      FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_STATION_ARRAY})
  void fromJson_setsExpectedResponseInfoFields(String givenFixture) {
    // given
    fixture.setupFixture(givenFixture);

    // when
    tankerkoenigResponse.fromJson(fixture.jsonFixture, TankerkoenigResponse.ResponseDto.class);

    // then
    fixture.assertEqualValues(tankerkoenigResponse);
  }

  @ParameterizedTest
  @ValueSource(strings = {
      FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_17STATIONS_HAPPY,
      FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY,
      FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_34STATIONS_HAPPY})
  void fromJson_setsExpectedLicenseStringOnHappyResponse(String givenFixture) {
    // given
    fixture.setupFixture(givenFixture);

    // when
    tankerkoenigResponse.fromJson(fixture.jsonFixture, TankerkoenigResponse.ResponseDto.class);

    // then
    assertEquals(
        fixture.objectFixture.getLicence(),
        tankerkoenigResponse.getTransactInfo().getLicence());
  }

  @Test
  void getLicenseString_returnsEmptyStringIfLicenceFieldIsNull() {
    // given
    fixture.setupFixture(FixtureFiles.TANKERKOENIG_JSON_RESPONSE_LONGITUDE_ERROR);

    // when
    tankerkoenigResponse.fromJson(fixture.jsonFixture, TankerkoenigResponse.ResponseDto.class);

    // then
    assertEquals("", tankerkoenigResponse.getTransactInfo().getLicence());
  }

  @Test
  void fromJson_setsCustomErrorMessageIfDeserializationResultIsNull() {
    // given
    String expectedMessagePart = "JSON string could not be converted";

    // when
    tankerkoenigResponse.fromJson(null, TankerkoenigResponse.ResponseDto.class);

    // then
    assertTrue(
        tankerkoenigResponse
            .getTransactInfo()
            .getErrorMessage()
            .orElse("")
            .contains(expectedMessagePart),
        "\nExpected error message to contain\n"
        + "\"" + expectedMessagePart + "\"\n"
        + "but actually is\n"
        + "\"" + tankerkoenigResponse.getTransactInfo().getErrorMessage() + "\"");
  }
}