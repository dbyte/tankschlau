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
 * This class offers an option to pass and persist one or more API keys from given properties.
 */
@Component
public class ApiKeyPropertyHandler {
  private static final Logger LOGGER = Logger.getLogger(ApiKeyPropertyHandler.class.getName());

  private final PropertyReader propertyReader;
  private final ApiKeyManager apiKeyManagerPetrolStations;
  private final ApiKeyManager apiKeyManagerGeocoding;
  private final ApiKeyManager apiKeyManagerPushMessage;
  private final UserPrefs userPrefs;

  @Autowired
  public ApiKeyPropertyHandler(
      PropertyReader propertyReader,
      ApiKeyManager apiKeyManagerPetrolStations,
      ApiKeyManager apiKeyManagerGeocoding,
      ApiKeyManager apiKeyManagerPushMessage,
      UserPrefs userPrefs) {

    this.propertyReader = propertyReader;
    this.apiKeyManagerPetrolStations = apiKeyManagerPetrolStations;
    this.apiKeyManagerGeocoding = apiKeyManagerGeocoding;
    this.apiKeyManagerPushMessage = apiKeyManagerPushMessage;
    this.userPrefs = userPrefs;
  }

  /**
   * Offers an option to pass and persist multiple API keys from given properties, e.g. from
   * variables of the VM environment. Valid keys for the values are:
   * <br><br>
   * <code style="color:yellow;">petrolStationsApiKey, geocodingApiKey, pushmessageApiKey,
   * pushmessageUserId</code>
   * <br><br>
   * Example for a valid single cmdline param:
   * <code>-DpetrolStationsApiKey="your-api-key-goes-here"</code>
   */
  public void persistApiKeys() {
    LOGGER.finest("Processing VM options");

    Optional.ofNullable(propertyReader.getProperty("petrolStationsApiKey"))
        .ifPresent(apiKeyManagerPetrolStations::write);

    Optional.ofNullable(propertyReader.getProperty("geocodingApiKey"))
        .ifPresent(apiKeyManagerGeocoding::write);

    Optional.ofNullable(propertyReader.getProperty("pushmessageApiKey"))
        .ifPresent(apiKeyManagerPushMessage::write);

    Optional.ofNullable(propertyReader.getProperty("pushmessageUserId"))
        .ifPresent(userPrefs::writePushMessageUserId);
  }
}
