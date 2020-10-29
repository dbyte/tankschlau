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

import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.user.ApiKeyManager;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.webserviceapi.common.MessageContent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
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
    this.sut = null;
    this.actualRequest = null;

    // Setup the mocks.
    this.apiKeyManagerMock = mock(ApiKeyManager.class);
    this.userPrefsMock = mock(UserPrefs.class);
    this.messageContentMock = mock(MessageContent.class);

    this.setupApiKeyAndUserIdMocks();
  }

  @Test
  void construct_setsExpectedValues() {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup
    assert userPrefsMock.readPushMessageUserId().isPresent(); // pre-check proper test setup

    // when
    actualRequest = new PushoverMessageRequest(apiKeyManagerMock, userPrefsMock);

    // then
    assertEquals("https://api.pushover.net/1/messages.json", actualRequest.getBaseUrl().toString());

    assertEquals(Request.HttpMethod.POST, actualRequest.getHttpMethod());

    assertEquals("application/json; charset=utf-8", actualRequest.getHeaders().get("Accept"));

    assertEquals(
        userPrefsMock.readPushMessageUserId().get(),
        actualRequest.getBodyParameters().get("user"));

    assertEquals(apiKeyManagerMock.read().get(), actualRequest.getBodyParameters().get("token"));
  }

  @Test
  void construct_doesNotAppendApiKeyToRequestBodyIfNoApiKeyWasFound() {
    // given
    when(apiKeyManagerMock.read()).thenReturn(Optional.empty());

    // when
    actualRequest = new PushoverMessageRequest(apiKeyManagerMock, userPrefsMock);

    // then
    assertNull(actualRequest.getBodyParameters().get("token"));
  }

  @Test
  void construct_doesNotAppendUserIdToRequestBodyIfNoUserIdWasFound() {
    // given
    when(userPrefsMock.readPushMessageUserId()).thenReturn(Optional.empty());

    // when
    actualRequest = new PushoverMessageRequest(apiKeyManagerMock, userPrefsMock);

    // then
    assertNull(actualRequest.getBodyParameters().get("user"));
  }

  @Test
  void construct_throwsOnNullArguments() {
    // when then
    assertThrows(NullPointerException.class, () -> new PushoverMessageRequest(null, null));
  }

  @Test
  void setMessage_trimsTitleAndMessageFields() {
    // given
    sut = new PushoverMessageRequest(apiKeyManagerMock, userPrefsMock);

    messageContentMock = mock(MessageContent.class);
    when(messageContentMock.getTitle()).thenReturn("    This title must be trimmed.     ");
    when(messageContentMock.getMessage()).thenReturn(" This message must be trimmed.         ");

    // when
    sut.setMessage(messageContentMock);

    // then
    assertEquals("This title must be trimmed.", sut.getBodyParameters().get("title"));
    assertEquals("This message must be trimmed.", sut.getBodyParameters().get("message"));
  }

  private void setupApiKeyAndUserIdMocks() {
    // Inject some API keys via VM Options if needed.
    String pmApiKey = Optional.ofNullable(System.getProperty("pushmessageApiKey"))
        .orElse("some-fake-abcdef-12345");

    String pmUserId = Optional.ofNullable(System.getProperty("pushmessageUserId"))
        .orElse("some-user-id-fake-aklmnop-666");

    // Re-init the mocks & add behaviour.
    this.apiKeyManagerMock = mock(ApiKeyManager.class);
    when(apiKeyManagerMock.read()).thenReturn(Optional.of(pmApiKey));

    this.userPrefsMock = mock(UserPrefs.class);
    when(userPrefsMock.readPushMessageUserId()).thenReturn(Optional.of(pmUserId));
  }
}