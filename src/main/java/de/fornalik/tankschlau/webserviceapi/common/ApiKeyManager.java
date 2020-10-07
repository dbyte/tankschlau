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

package de.fornalik.tankschlau.webserviceapi.common;

import de.fornalik.tankschlau.util.StringLegalizer;

import java.util.Objects;
import java.util.Optional;

/**
 * Class for API key handling.
 */
public class ApiKeyManager {

  /**
   * Token to unambiguously identify the api key within {@link #apiKeyStore}.
   */
  private final String id;
  /**
   * Storage strategy for the API key.
   */
  private final ApiKeyStore apiKeyStore;

  /**
   * Constructor. In production, use static factory methods to get a new instance, for example
   * {@link #createForGeocoding(ApiKeyStore)}, {@link #createForPetrolStations(ApiKeyStore)}.
   *
   * @param apiKeyStore Storage strategy for the API key.
   * @param id          Unique identifier for the API key within a sort of data collection.
   */
  private ApiKeyManager(ApiKeyStore apiKeyStore, String id) {
    this.apiKeyStore = Objects.requireNonNull(apiKeyStore);
    this.id = StringLegalizer.create(id).mandatory().end();
  }

  /**
   * Returns a new instance of ApiKeyManager for use with a petrol stations webservice.
   *
   * @param apiKeyStore Storage strategy for the API key.
   * @return New instance of ApiKeyManager with a predefined {@link #id}.
   */
  public static ApiKeyManager createForPetrolStations(ApiKeyStore apiKeyStore) {
    return new ApiKeyManager(apiKeyStore, "apikey.petrolstations");
  }

  /**
   * Returns a new instance of ApiKeyManager for use with a geocoding webservice.
   *
   * @param apiKeyStore Storage strategy for the API key.
   * @return New instance of ApiKeyManager with a predefined {@link #id}.
   */
  public static ApiKeyManager createForGeocoding(ApiKeyStore apiKeyStore) {
    return new ApiKeyManager(apiKeyStore, "apikey.geocoding");
  }

  /**
   * Returns a new instance of ApiKeyManager for use with a push messenger webservice.
   *
   * @param apiKeyStore Storage strategy for the API key.
   * @return New instance of ApiKeyManager with a predefined {@link #id}.
   */
  public static ApiKeyManager createForPushMessage(ApiKeyStore apiKeyStore) {
    return new ApiKeyManager(apiKeyStore, "apikey.pushmessage");
  }

  public ApiKeyStore getApiKeyStore() {
    return apiKeyStore;
  }

  public String getId() {
    return id;
  }

  /**
   * Reads the API key from storage.
   *
   * @return The API key itself as an Optional, or an empty Optional if the key not exists in
   * storage.
   */
  public Optional<String> read() {
    return apiKeyStore.read(id);
  }

  /**
   * Writes a given API key into the storage.
   *
   * @param apiKey The API key to write into the storage.
   */
  public void write(String apiKey) {
    apiKeyStore.write(id, StringLegalizer.create(apiKey).nullToEmpty().end());
  }
}
