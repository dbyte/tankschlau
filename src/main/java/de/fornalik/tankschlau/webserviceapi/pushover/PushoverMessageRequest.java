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
import de.fornalik.tankschlau.net.PushMessageRequest;
import de.fornalik.tankschlau.util.StringLegalizer;
import de.fornalik.tankschlau.util.UserPrefs;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;

import java.util.Objects;

// TODO unit test, javadoc
public class PushoverMessageRequest extends PushMessageRequest {
  private static final String BASE_URL = "https://api.pushover.net/1/messages.json";

  private final ApiKeyManager apiKeyManager;
  private final UserPrefs userPrefs;

  public PushoverMessageRequest() {
    this(TankSchlau.GEOCODING_APIKEY_MANAGER, TankSchlau.USER_PREFS);
  }

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
    addHeader("Accept", "application/json; charset=utf-8");
  }

  private void setAuthenticationParameters() {
    /* Only add user key if we got one. Pushover will inform us about a missing/invalid user key
    in its response, where we handle errors anyway. */
    userPrefs.readPushMessengerUserId().ifPresent(value -> addBodyParameter("user", value));

    // Dito for API key.
    apiKeyManager.read().ifPresent(value -> addBodyParameter("token", value));
  }

  @Override
  public void setMessage(String message) {
    message = StringLegalizer.create(message).safeTrim().nullToEmpty().end();
    addBodyParameter("message", message);
  }
}
