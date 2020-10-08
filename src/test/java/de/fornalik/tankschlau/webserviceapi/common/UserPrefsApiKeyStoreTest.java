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

import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.user.UserPrefsApiKeyStore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class UserPrefsApiKeyStoreTest {
  private static UserPrefs userPrefsMock;
  private UserPrefsApiKeyStore userPrefsApiKeyStore;
  private String givenUserPrefId, expectedApiKey;

  @BeforeAll
  static void beforeAll() {
    userPrefsMock = mock(UserPrefs.class);
  }

  @AfterAll
  static void afterAll() {
    userPrefsMock = null;
  }

  @BeforeEach
  void setUp() {
    this.givenUserPrefId = "some.apikey.id";
    this.expectedApiKey = "apikey-value-0000-123546-9999";

    userPrefsApiKeyStore = new UserPrefsApiKeyStore(userPrefsMock);

    when(
        userPrefsMock.readApiKey(givenUserPrefId))
        .thenReturn(Optional.of(expectedApiKey));
  }

  @Test
  void read_returnsApiKeyFromUserPrefs() {
    // when
    Optional<String> actualApiKey = userPrefsApiKeyStore.read(givenUserPrefId);

    // then
    assertTrue(actualApiKey.isPresent());
    assertEquals(Optional.of(expectedApiKey), actualApiKey);

    inOrder(userPrefsMock)
        .verify(
            userPrefsMock,
            calls(1))

        .readApiKey(givenUserPrefId);
  }

  @Test
  void write_properlyCallsUserPrefs() {
    // when
    userPrefsApiKeyStore.write(givenUserPrefId, expectedApiKey);

    // when then
    inOrder(userPrefsMock)
        .verify(
            userPrefsMock,
            calls(1))

        .writeApiKey(givenUserPrefId, expectedApiKey);
  }
}