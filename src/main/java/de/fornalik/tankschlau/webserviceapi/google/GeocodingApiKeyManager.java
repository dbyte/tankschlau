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

package de.fornalik.tankschlau.webserviceapi.google;

import de.fornalik.tankschlau.webserviceapi.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.ApiKeyStore;

// TODO unit tests

/**
 * Implementation for API key handling of
 * <a href="https://developers.google.com/maps/documentation/geocoding/start">Google Geocoding</a>
 * web services.
 */
public class GeocodingApiKeyManager extends ApiKeyManager {

  /**
   * Constructor
   *
   * @param apiKeyStore Storage strategy for the API key.
   */
  public GeocodingApiKeyManager(ApiKeyStore apiKeyStore) {
    super(apiKeyStore, "apiKey.geoService");
  }
}
