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

package de.fornalik.tankschlau.webserviceapi.pushover;

import com.google.gson.Gson;
import de.fornalik.tankschlau.net.Response;
import de.fornalik.tankschlau.net.ResponseBody;
import de.fornalik.tankschlau.storage.TransactInfo;
import de.fornalik.tankschlau.storage.TransactInfoImpl;
import de.fornalik.tankschlau.testhelp_common.FixtureFiles;
import de.fornalik.tankschlau.testhelp_common.PushoverFixtureHelp;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class PushoverMessageResponseTest {
  private static Gson jsonProvider;
  private Response actualResponse;
  private PushoverFixtureHelp fixture;
  private PushoverMessageResponse pushoverMessageResponse;

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

    // No mock - need real object here
    TransactInfo transactInfoMock = new TransactInfoImpl();

    this.pushoverMessageResponse = new PushoverMessageResponse(
        jsonProvider,
        responseBodyMock,
        transactInfoMock); // SUT

    this.actualResponse = null;
    this.fixture = new PushoverFixtureHelp();
  }

  @Test
  void construct_throwsImmediatelyOnNullArguments() {
    assertThrows(NullPointerException.class, () -> new PushoverMessageResponse(null, null, null));
  }

  @ParameterizedTest
  @ValueSource(strings = {
      FixtureFiles.PUSHOVER_RESPONSE_STATUS_1,
      FixtureFiles.PUSHOVER_RESPONSE_STATUS_0_INVALID_SECRET,
      FixtureFiles.PUSHOVER_RESPONSE_STATUS_0_INVALID_TOKEN,
      FixtureFiles.PUSHOVER_RESPONSE_STATUS_0_INVALID_USER})
  void fromJson_deserializesJsonDResponseDataTransferObject_happy(String givenJsonString) {
    // given
    fixture.setupFixture(givenJsonString);

    // when
    pushoverMessageResponse.fromJson(fixture.jsonFixture, Void.class);

    System.out.println(pushoverMessageResponse.getTransactInfo().getErrorMessage());

    // then
    assertNotNull(pushoverMessageResponse.getTransactInfo());
    fixture.assertEqualValues(pushoverMessageResponse.getTransactInfo());
  }

  @Test
  void fromJson_() {
  }
}
