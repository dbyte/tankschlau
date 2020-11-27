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

package de.fornalik.tankschlau.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * This class offers an option to pass and handle one or more API keys at startup.
 */
@Component
public class ApiKeyVmOptionHandler {
  private static final Logger LOGGER = Logger.getLogger(ApiKeyVmOptionHandler.class.getName());

  private final ApiKeyManager apiKeyManagerPetrolStations;
  private final ApiKeyManager apiKeyManagerGeocoding;
  private final ApiKeyManager apiKeyManagerPushMessage;
  private final UserPrefs userPrefs;

  @Autowired
  public ApiKeyVmOptionHandler(
      ApiKeyManager apiKeyManagerPetrolStations,
      ApiKeyManager apiKeyManagerGeocoding,
      ApiKeyManager apiKeyManagerPushMessage,
      UserPrefs userPrefs) {

    this.apiKeyManagerPetrolStations = apiKeyManagerPetrolStations;
    this.apiKeyManagerGeocoding = apiKeyManagerGeocoding;
    this.apiKeyManagerPushMessage = apiKeyManagerPushMessage;
    this.userPrefs = userPrefs;
  }

  /**
   * Offers an option to pass and persist multiple API keys at startup.
   * Valid keys for the values are:
   * <code>petrolStationsApiKey, geocodingApiKey, pushmessageApiKey, pushmessageUserId</code>
   * <br><br>
   * Example for a single cmdline param:
   * <code>-DpetrolStationsApiKey="your-api-key-goes-here"</code>
   */
  public void processVmOptions() {
    LOGGER.finest("Processing VM options");

    Optional.ofNullable(System.getProperty("petrolStationsApiKey"))
        .ifPresent(apiKeyManagerPetrolStations::write);

    Optional.ofNullable(System.getProperty("geocodingApiKey"))
        .ifPresent(apiKeyManagerGeocoding::write);

    Optional.ofNullable(System.getProperty("pushmessageApiKey"))
        .ifPresent(apiKeyManagerPushMessage::write);

    Optional.ofNullable(System.getProperty("pushmessageUserId"))
        .ifPresent(userPrefs::writePushMessageUserId);
  }
}
