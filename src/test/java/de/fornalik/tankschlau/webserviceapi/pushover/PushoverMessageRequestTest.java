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

import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.OkHttpClient;
import de.fornalik.tankschlau.net.Response;
import de.fornalik.tankschlau.util.UserPrefs;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PushoverMessageRequestTest {
  UserPrefs userPrefsMock;
  private PushoverMessageRequest actualRequest;
  private ApiKeyManager apiKeyManagerMock;

  @BeforeEach
  void setUp() {
    this.actualRequest = null;

    this.apiKeyManagerMock = mock(ApiKeyManager.class);
    when(apiKeyManagerMock.read()).thenReturn(Optional.of("abcdef-12345"));

    this.userPrefsMock = mock(UserPrefs.class);
    when(userPrefsMock.readPushMessengerUserId()).thenReturn(Optional.of("klmnop-666"));
  }

  @Test
  void testTheBest() throws IOException {
    // given
    actualRequest = new PushoverMessageRequest(apiKeyManagerMock, userPrefsMock);

    // when
    actualRequest.setMessage("UTF-8? Umlauts! ÖÄÜ öäü ß.");

    // then
    HttpClient httpClient = new OkHttpClient();
    Response response = httpClient.newCall(actualRequest);
    System.out.println(response.getBody());
  }

  @Test
  void setMessage_XXX() {
    fail();
  }
}