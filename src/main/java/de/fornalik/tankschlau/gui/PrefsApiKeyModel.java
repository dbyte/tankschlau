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

package de.fornalik.tankschlau.gui;

import de.fornalik.tankschlau.user.ApiKeyManager;
import de.fornalik.tankschlau.user.UserPrefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class PrefsApiKeyModel {

  private final UserPrefs userPrefs;
  private final ApiKeyManager apiKeyManagerPetrolStations;
  private final ApiKeyManager apiKeyManagerGeocoding;
  private final ApiKeyManager apiKeyManagerPushMessage;

  @Autowired
  PrefsApiKeyModel(
      UserPrefs userPrefs,
      ApiKeyManager apiKeyManagerPetrolStations,
      ApiKeyManager apiKeyManagerGeocoding,
      ApiKeyManager apiKeyManagerPushMessage) {

    this.userPrefs = userPrefs;
    this.apiKeyManagerPetrolStations = apiKeyManagerPetrolStations;
    this.apiKeyManagerGeocoding = apiKeyManagerGeocoding;
    this.apiKeyManagerPushMessage = apiKeyManagerPushMessage;
  }

  String readPetrolStationsApiKey() {
    return apiKeyManagerPetrolStations.read().orElse("");
  }

  void writePetrolStationsApiKey(String value) {
    apiKeyManagerPetrolStations.write(value);
  }

  String readGeocodingApiKey() {
    return apiKeyManagerGeocoding.read().orElse("");
  }

  void writeGeocodingApiKey(String value) {
    apiKeyManagerGeocoding.write(value);
  }

  String readPushmessageApiKey() {
    return apiKeyManagerPushMessage.read().orElse("");
  }

  void writePushmessageApiKey(String value) {
    apiKeyManagerPushMessage.write(value);
  }

  String readPushmessageUserId() {
    return userPrefs.readPushMessageUserId().orElse("");
  }

  void writePushmessageUserId(String value) {
    userPrefs.writePushMessageUserId(value);
  }

}
