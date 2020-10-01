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

package de.fornalik.tankschlau.webserviceapi;

import java.util.Optional;

/**
 * Interface for API key storage implementations.
 */
public interface ApiKeyStore {

  /**
   * Reads an API key from the store.
   *
   * @param id A token to identify which API key to return from the storage.
   * @return Value of the API key
   */
  Optional<String> read(String id);

  /**
   * Writes an API key to the store.
   *
   * @param id     A token to identify which API key to write into the storage.
   * @param apiKey The value of the API key
   */
  void write(String id, String apiKey);
}
