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

import de.fornalik.tankschlau.net.JsonRequest;
import de.fornalik.tankschlau.net.JsonRequestImpl;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Global;
import de.fornalik.tankschlau.util.StringLegalizer;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.common.MessageContent;
import de.fornalik.tankschlau.webserviceapi.common.MessageRequest;

import java.util.Objects;

public class PushoverMessageRequest extends JsonRequestImpl implements MessageRequest, JsonRequest {
  private static final String BASE_URL = "https://api.pushover.net/1/messages.json";

  private final ApiKeyManager apiKeyManager;
  private final UserPrefs userPrefs;

  public PushoverMessageRequest(ApiKeyManager apiKeyManager, UserPrefs userPrefs) {
    super();

    this.apiKeyManager = Objects.requireNonNull(
        apiKeyManager,
        "apiKeyManager must not be null.");

    this.userPrefs = Objects.requireNonNull(
        userPrefs,
        "userPrefs must not be null.");

    setBaseData();
    setHeaders();
    setAuthenticationParameters();
  }

  private void setBaseData() {
    setBaseUrl(StringLegalizer.create(BASE_URL).toUrl());
    setHttpMethod(HttpMethod.POST);
  }

  private void setHeaders() {
    putHeader("Accept", "application/json; charset=utf-8");
    putBodyParameter("User-Agent", Global.APP_NAME);
  }

  private void setAuthenticationParameters() {
    /* Only add user key if we got one. Pushover will inform us about a missing/invalid user key
    in its response, where we handle errors anyway. */
    userPrefs.readPushMessageUserId().ifPresent(value -> putBodyParameter("user", value));

    // Dito for API key.
    apiKeyManager.read().ifPresent(value -> putBodyParameter("token", value));
  }

  @Override
  public void setMessage(MessageContent content) {
    String title = StringLegalizer.create(content.getTitle()).safeTrim().nullToEmpty().end();
    String message = StringLegalizer.create(content.getMessage()).safeTrim().nullToEmpty().end();
    putBodyParameter("title", title);
    putBodyParameter("message", message);
  }
}
