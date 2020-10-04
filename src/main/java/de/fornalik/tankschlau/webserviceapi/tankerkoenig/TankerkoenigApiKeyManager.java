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

package de.fornalik.tankschlau.webserviceapi.tankerkoenig;

import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyStore;

// TODO unit tests

/**
 * Implementation for API key handling of the tankerkoenig.de web service
 */
public class TankerkoenigApiKeyManager extends ApiKeyManager {

  /**
   * Constructor
   *
   * @param apiKeyStore Storage strategy for the API key.
   */
  public TankerkoenigApiKeyManager(ApiKeyStore apiKeyStore) {
    super(apiKeyStore, "apiKey.tankerkoenig");
  }
}
