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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class ApiKeyManagerTest {
  private static ApiKeyStore apiKeyStoreMock;
  private String expectedApiKeyForPetrolStations, expectedApiKeyForGeocoding,
      expectedApiKeyForPushMessenger;
  private ApiKeyManager apiKeyManager;
  private String actualApiKey;

  @BeforeAll
  static void beforeAll() {
    apiKeyStoreMock = mock(ApiKeyStore.class);

  }

  @AfterAll
  static void afterAll() {
    apiKeyStoreMock = null;
  }

  @BeforeEach
  void setUp() {
    this.actualApiKey = null;
    this.apiKeyManager = null;

    this.expectedApiKeyForPetrolStations = "apikey-value-fake-for-petrolstations";
    this.expectedApiKeyForGeocoding = "apikey-value-fake-for-geocoding";
    this.expectedApiKeyForPushMessenger = "apikey-value-fake-for-pushmessenger";

    when(apiKeyStoreMock.read("apikey.petrolstations"))
        .thenReturn(Optional.of(expectedApiKeyForPetrolStations));

    when(apiKeyStoreMock.read("apikey.geocoding"))
        .thenReturn(Optional.of(expectedApiKeyForGeocoding));

    when(apiKeyStoreMock.read("apikey.pushmessenger"))
        .thenReturn(Optional.of(expectedApiKeyForPushMessenger));
  }

  @Test
  void createForPetrolStations_setsFieldsProperly() {
    // given
    String expectedId = "apikey.petrolstations";
    ApiKeyStore expectedApiKeyStore = apiKeyStoreMock;

    // when
    apiKeyManager = ApiKeyManager.createForPetrolStations(expectedApiKeyStore);

    // then
    assertEquals(expectedApiKeyStore, apiKeyManager.getApiKeyStore());
    assertEquals(expectedId, apiKeyManager.getId());
  }

  @Test
  void createForGeocoding_setsFieldsProperly() {
    // given
    String expectedId = "apikey.geocoding";
    ApiKeyStore expectedApiKeyStore = apiKeyStoreMock;

    // when
    apiKeyManager = ApiKeyManager.createForGeocoding(expectedApiKeyStore);

    // then
    assertEquals(expectedApiKeyStore, apiKeyManager.getApiKeyStore());
    assertEquals(expectedId, apiKeyManager.getId());
  }

  @Test
  void createForPushMessenger_setsFieldsProperly() {
    // given
    String expectedId = "apikey.pushmessenger";
    ApiKeyStore expectedApiKeyStore = apiKeyStoreMock;

    // when
    apiKeyManager = ApiKeyManager.createForPushMessenger(expectedApiKeyStore);

    // then
    assertEquals(expectedApiKeyStore, apiKeyManager.getApiKeyStore());
    assertEquals(expectedId, apiKeyManager.getId());
  }

  @Test
  void create_throwsOnApiKeyStoreNullArgument() {
    assertThrows(
        NullPointerException.class,
        () -> ApiKeyManager.createForPetrolStations(null));

    assertThrows(
        NullPointerException.class,
        () -> ApiKeyManager.createForGeocoding(null));
  }

  @Test
  void read_forPetrolStations_returnsProperApiKey() {
    // given
    apiKeyManager = ApiKeyManager.createForPetrolStations(apiKeyStoreMock);

    // when
    //noinspection OptionalGetWithoutIsPresent
    actualApiKey = apiKeyManager.read().get();

    // then
    assertEquals(expectedApiKeyForPetrolStations, actualApiKey);
  }

  @Test
  void read_forGeocoding_returnsProperApiKey() {
    // given
    apiKeyManager = ApiKeyManager.createForGeocoding(apiKeyStoreMock);

    // when
    //noinspection OptionalGetWithoutIsPresent
    actualApiKey = apiKeyManager.read().get();

    // then
    assertEquals(expectedApiKeyForGeocoding, actualApiKey);
  }

  @Test
  void read_forPushMessenger_returnsProperApiKey() {
    // given
    apiKeyManager = ApiKeyManager.createForPushMessenger(apiKeyStoreMock);

    // when
    //noinspection OptionalGetWithoutIsPresent
    actualApiKey = apiKeyManager.read().get();

    // then
    assertEquals(expectedApiKeyForPushMessenger, actualApiKey);
  }

  @Test
  void write_forPetrolStations_properlyPassesArgumentsToApiKeyStore() {
    // given
    String expectedId = "apikey.petrolstations";
    String expectedApiKey = "my-api-key-007";

    apiKeyManager = ApiKeyManager.createForPetrolStations(apiKeyStoreMock);

    // when
    apiKeyManager.write(expectedApiKey);

    // then
    Mockito.inOrder(apiKeyStoreMock)
           .verify(
               apiKeyStoreMock,
               calls(1))
           .write(expectedId, expectedApiKey);
  }

  @Test
  void write_forGeocoding_properlyPassesArgumentsToApiKeyStore() {
    // given
    String expectedId = "apikey.geocoding";
    String expectedApiKey = "my-api-key-39913293912837";

    apiKeyManager = ApiKeyManager.createForGeocoding(apiKeyStoreMock);

    // when
    apiKeyManager.write(expectedApiKey);

    // then
    inOrder(apiKeyStoreMock)
        .verify(
            apiKeyStoreMock,
            calls(1))

        .write(expectedId, expectedApiKey);
  }

  @Test
  void write_forPushMessenger_properlyPassesArgumentsToApiKeyStore() {
    // given
    String expectedId = "apikey.pushmessenger";
    String expectedApiKey = "my-api-key-8sksnsis-29eell";

    apiKeyManager = ApiKeyManager.createForPushMessenger(apiKeyStoreMock);

    // when
    apiKeyManager.write(expectedApiKey);

    // then
    inOrder(apiKeyStoreMock)
        .verify(
            apiKeyStoreMock,
            calls(1))

        .write(expectedId, expectedApiKey);
  }
}