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

import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.common.MessageContent;
import org.junit.jupiter.api.BeforeEach;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PushoverMessageRequestTest {
  private PushoverMessageRequest sut;
  private PushoverMessageRequest actualRequest;
  private MessageContent messageContentMock;
  private UserPrefs userPrefsMock;
  private ApiKeyManager apiKeyManagerMock;

  @BeforeEach
  void setUp() {
    this.actualRequest = null;

    // Setup the mocks.
    this.apiKeyManagerMock = mock(ApiKeyManager.class);
    this.userPrefsMock = mock(UserPrefs.class);
    this.messageContentMock = mock(MessageContent.class);

    this.sut = new PushoverMessageRequest(apiKeyManagerMock, userPrefsMock);
  }

  private void helpIntegrationTestSetup() {
    // Inject some API keys via VM Options if needed.
    String pmApiKey = Optional.ofNullable(System.getProperty("pushmessageApiKey"))
        .orElse("some-fake-abcdef-12345");

    String pmUserId = Optional.ofNullable(System.getProperty("pushmessageUserId"))
        .orElse("some-fake-aklmnop-666");

    // Re-init the mocks & add behaviour.
    this.apiKeyManagerMock = mock(ApiKeyManager.class);
    when(apiKeyManagerMock.read()).thenReturn(Optional.of(pmApiKey));

    this.userPrefsMock = mock(UserPrefs.class);
    when(userPrefsMock.readPushMessageUserId()).thenReturn(Optional.of(pmUserId));
  }
}