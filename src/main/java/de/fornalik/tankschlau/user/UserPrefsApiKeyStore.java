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

import de.fornalik.tankschlau.webserviceapi.common.ApiKeyStore;

import java.util.Optional;

/**
 * Implementation for {@link UserPrefs} based API key storage.
 *
 * @implNote Note this implementation is a <span style="color:red;">security risk</span>
 * and therefore should only be used for demo and testing purposes.
 */
public class UserPrefsApiKeyStore implements ApiKeyStore {
  private final UserPrefs userPrefs;

  public UserPrefsApiKeyStore(UserPrefs userPrefs) {
    this.userPrefs = userPrefs;
  }

  @Override
  public Optional<String> read(String id) {
    return userPrefs.readApiKey(id);
  }

  @Override
  public void write(String id, String apiKey) {
    userPrefs.writeApiKey(id, apiKey);
  }
}
