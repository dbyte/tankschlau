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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class ApiKeyPropertyHandlerTest {
  private ApiKeyPropertyHandler sut;
  private PropertyReader mockPropertyReader;
  private ApiKeyManager mockApiKeyManagerPetrolStations;
  private ApiKeyManager mockApiKeyManagerGeocoding;
  private ApiKeyManager mockApiKeyManagerPushMessage;
  private UserPrefs mockUserPrefs;

  @BeforeEach
  void setUp() {
    mockPropertyReader = mock(PropertyReader.class);
    mockApiKeyManagerPetrolStations = mock(ApiKeyManager.class);
    mockApiKeyManagerGeocoding = mock(ApiKeyManager.class);
    mockApiKeyManagerPushMessage = mock(ApiKeyManager.class);
    mockUserPrefs = mock(UserPrefs.class);

    sut = new ApiKeyPropertyHandler(
        mockPropertyReader,
        mockApiKeyManagerPetrolStations,
        mockApiKeyManagerGeocoding,
        mockApiKeyManagerPushMessage,
        mockUserPrefs
    );
  }

  @Test
  void processVmOptions_mapsGivenPropertyKeysAsExpectedAndCallsWriteMethodsWithCorrespondingValues() {
    // given
    when(mockPropertyReader.getProperty("petrolStationsApiKey"))
        .thenReturn("PetrolStationsApiKey-012345-6789");

    when(mockPropertyReader.getProperty("geocodingApiKey"))
        .thenReturn("GeocodingApiKey-012345-6789");

    when(mockPropertyReader.getProperty("pushmessageApiKey"))
        .thenReturn("PushmessageApiKey-012345-6789");

    when(mockPropertyReader.getProperty("pushmessageUserId"))
        .thenReturn("PushmessageUserId-012345-6789");

    // when
    sut.persistApiKeys();

    // then
    verify(mockApiKeyManagerPetrolStations, times(1))
        .write("PetrolStationsApiKey-012345-6789");

    verify(mockApiKeyManagerGeocoding, times(1))
        .write("GeocodingApiKey-012345-6789");

    verify(mockApiKeyManagerPushMessage, times(1))
        .write("PushmessageApiKey-012345-6789");

    verify(mockUserPrefs, times(1))
        .writePushMessageUserId("PushmessageUserId-012345-6789");
  }
}