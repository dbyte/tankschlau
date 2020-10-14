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
import de.fornalik.tankschlau.net.Response;
import de.fornalik.tankschlau.storage.TransactInfo;
import de.fornalik.tankschlau.webserviceapi.common.MessageContent;
import de.fornalik.tankschlau.webserviceapi.common.MessageRequest;
import de.fornalik.tankschlau.webserviceapi.common.MessageService;

import java.util.Objects;

/**
 * Implementation of {@link MessageService} for pushover.net push message webservices.
 *
 * @see
 * <a href="https://pushover.net/api">Pushover Message API documentation: https://pushover.net/api</a>
 */
// TODO unit test, javadoc
public class PushoverMessageService implements MessageService {
  private final HttpClient httpClient;
  private final MessageRequest request;
  private final Response response;

  /**
   * Constructor
   *
   * @param httpClient Some implementation of {@link HttpClient} for interaction with webservice.
   * @param request    Some implementation of {@link MessageRequest}, forming a concrete request.
   */
  public PushoverMessageService(HttpClient httpClient, MessageRequest request, Response response) {
    this.httpClient = Objects.requireNonNull(httpClient);
    this.request = Objects.requireNonNull(request);
    this.response = Objects.requireNonNull(response);
  }

  @Override
  public Response sendMessage(MessageContent content) {
    request.setMessage(content);
    response.reset();

    // It's guaranteed by newCall(...) that returned response is not null.
    httpClient.newCall(request, response, String.class);

     /*
    Note: After newCall, the field response.transactInfo may already contain error message etc,
    mutated by the http client while processing communication/request/response.
    */
    Objects.requireNonNull(response, "Response is null.");

    /*
    At this point we assert a valid JSON document - well formed and determined
    by the webservice's API. So all following processing should crash only if _we_
    messed things up.
    */
    response
        .getTransactInfo()
        .getErrorMessage()
        .ifPresent((msg) -> System.out.println("Log.Error: " + msg));

    return response;
  }

  @Override
  public TransactInfo getTransactInfo() {
    throw new UnsupportedOperationException("To be implemented.");
  }
}
