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

import de.fornalik.tankschlau.TankSchlau;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.MessageRequest;
import de.fornalik.tankschlau.net.Response;
import de.fornalik.tankschlau.net.StringResponse;
import de.fornalik.tankschlau.webserviceapi.common.MessageClient;
import de.fornalik.tankschlau.webserviceapi.common.MessageContent;

import java.io.IOException;

/**
 * Implementation of {@link MessageClient} for pushover.net push message webservices.
 *
 * @see <a href="https://pushover.net/api">Pushover Message API documentation</a>
 */
// TODO unit test, javadoc
public class PushoverMessageClient implements MessageClient {
  private final HttpClient httpClient;
  private final MessageRequest request;

  /**
   * <span style="color:yellow;">Use this constructor for production</span> as it implicitly
   * uses predefined static dependencies defined at startup.
   *
   * @see #PushoverMessageClient(HttpClient, MessageRequest)
   */
  public PushoverMessageClient() {
    this(TankSchlau.HTTP_CLIENT, new PushoverMessageRequest());
  }

  /**
   * Dependency injection variant of {@link #PushoverMessageClient()},
   * <span style="color:yellow;">e.g. for testing purposes.</span>
   *
   * @param httpClient Some implementation of {@link HttpClient} for interaction with webservice.
   * @param request    Some implementation of {@link MessageRequest}, forming a concrete request.
   * @see #PushoverMessageClient()
   */
  public PushoverMessageClient(HttpClient httpClient, MessageRequest request) {
    this.httpClient = httpClient;
    this.request = request;
  }

  @Override
  public Response sendMessage(MessageContent content) throws IOException {
    request.setMessage(content);
    StringResponse response = (StringResponse) httpClient.newCall(request);

    if (response == null) {
      StringResponse resp = StringResponse.create();
      resp.setErrorMessage("Response is null.");
      return resp;
    }

    if (!response.getBody().isPresent()) {
      response.setErrorMessage(response.getErrorMessage().orElse("Response body is empty."));
    }

    return response;
  }
}
