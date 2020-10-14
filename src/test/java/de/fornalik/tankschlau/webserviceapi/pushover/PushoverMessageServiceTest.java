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
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.OkHttpClient;
import de.fornalik.tankschlau.net.Response;
import de.fornalik.tankschlau.net.ResponseBodyImpl;
import de.fornalik.tankschlau.storage.TransactInfoImpl;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.common.MessageContent;
import de.fornalik.tankschlau.webserviceapi.common.MessageRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PushoverMessageServiceTest {
  private static HttpClient httpClientMock;
  private static Gson jsonProvider;
  private static MessageRequest messageRequestMock;

  private PushoverMessageService messageClient;
  private MessageContent messageContentMock;
  private PushoverMessageResponse messageResponseMock;
  private UserPrefs userPrefsMock;
  private ApiKeyManager apiKeyManagerMock;

  @BeforeAll
  static void beforeAll() {
    jsonProvider = new Gson();
    httpClientMock = mock(HttpClient.class);
    messageRequestMock = mock(MessageRequest.class);
  }

  @AfterAll
  static void afterAll() {
    jsonProvider = null;
    httpClientMock = null;
    messageRequestMock = null;
  }

  @BeforeEach
  void setUp() {
    this.messageContentMock = mock(MessageContent.class);
    this.messageResponseMock = mock(PushoverMessageResponse.class);

    this.messageClient = new PushoverMessageService(
        httpClientMock,
        messageRequestMock,
        messageResponseMock);

  }

  @Test
  void sendMessage_shouldCrashWithNullPointerExceptionIfResponseIsNull() {
    // given
    when(httpClientMock.newCall(any(), any(), any())).thenReturn(null);

    // when then
    assertThrows(NullPointerException.class, () -> messageClient.sendMessage(messageContentMock));
  }

  @Test
  void sendMessage_integrationTest_shouldSendRealMessage() {
    // given
    HttpClient realHttpClient = new OkHttpClient(new okhttp3.OkHttpClient());

    PushoverMessageResponse realResponse = new PushoverMessageResponse(
        jsonProvider,
        new ResponseBodyImpl(),
        new TransactInfoImpl());

    helpIntegrationTestSetup();
    MessageRequest realRequest = new PushoverMessageRequest(apiKeyManagerMock, userPrefsMock);

    PushoverMessageService messageClient = new PushoverMessageService(
        realHttpClient,
        realRequest,
        realResponse);

    MessageContent realMessageContent = new PushoverMessageContent(null);
    realMessageContent.setTitle("New price for station!");
    realMessageContent.setMessage("UTF-8? Umlauts! ÖÄÜ öäü ß.\nThis should be a new line");

    // when
    Response response = messageClient.sendMessage(realMessageContent);

    // then
    System.out.println("RESPONSE BODY: " + response.getBody().getData(String.class));
    System.out.println("RESPONSE STATUS: " + response.getTransactInfo().getStatus());
    System.out.println("RESPONSE ERROR MESSAGE: " + response.getTransactInfo().getErrorMessage());
  }

  // TODO more unit tests go here

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