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

// TODO unit tests

/**
 * Abstract base class for API key handling.
 */
public abstract class ApiKeyManager {
  protected final String id;
  protected final ApiKeyStore apiKeyStore;

  /**
   * Constructor
   *
   * @param apiKeyStore Storage strategy for the API key.
   * @param id          Unique identifier for the API key within a sort of data collection.
   */
  protected ApiKeyManager(ApiKeyStore apiKeyStore, String id) {
    this.apiKeyStore = Objects.requireNonNull(apiKeyStore);
    this.id = StringLegalizer.create(id).mandatory().end();
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
