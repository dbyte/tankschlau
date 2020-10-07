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

import de.fornalik.tankschlau.net.PushMessageRequest;
import de.fornalik.tankschlau.util.StringLegalizer;

// TODO unit test, javadoc
public class PushoverMessageRequest extends PushMessageRequest {
  private static final String BASE_URL = "https://api.pushover.net/1/messages.json";
  private static final HttpMethod HTTP_METHOD = HttpMethod.POST;

  public PushoverMessageRequest() {
    super();

    setBaseData();
  }

  private void setBaseData() {
    setBaseUrl(StringLegalizer.create(BASE_URL).mandatory().toUrl());
    setHttpMethod(HTTP_METHOD);
  }

  @Override
  public void setMessageParameter(String message) {

  }
}
