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

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.webserviceapi.ApiKeyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

/**
 * We only test for concrete implementation specific values here. All other behaviour should
 * be directly tested in unit tests for {@link de.fornalik.tankschlau.net.BaseRequest}
 */
class TankerkoenigRequestTest {
  private ApiKeyManager apiKeyManagerMock;
  private Geo geoFixture;

  @BeforeEach
  void setUp() {
    this.apiKeyManagerMock = Mockito.mock(ApiKeyManager.class);
    this.geoFixture = new Geo(53.1234, 48.5678, 12.0);

    Mockito.when(apiKeyManagerMock.read()).thenReturn(Optional.of("000-abc-def-111"));
  }

  @Test
  void create_constructsProperValues() throws MalformedURLException {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup

    // when
    TankerkoenigRequest actualRequest = TankerkoenigRequest.create(apiKeyManagerMock, geoFixture);

    // then
    assertEquals(
        "https://creativecommons.tankerkoenig.de/json/list.php?",
        actualRequest.getBaseUrl().toString());

    assertEquals(Request.HttpMethod.GET, actualRequest.getHttpMethod());

    assertEquals("application/json; charset=utf-8", actualRequest.getHeaders().get("Accept"));

    assertEquals("dist", actualRequest.getUrlParameters().get("sort"));
    assertEquals("all", actualRequest.getUrlParameters().get("type"));
    assertEquals(apiKeyManagerMock.read().get(), actualRequest.getUrlParameters().get("apikey"));

    assertEquals(
        geoFixture.getLatitude(),
        Double.valueOf(actualRequest.getUrlParameters().get("lat")));

    assertEquals(
        geoFixture.getLongitude(),
        Double.valueOf(actualRequest.getUrlParameters().get("lng")));

    assertEquals(
        geoFixture.getDistance(),
        Optional.of(Double.valueOf(actualRequest.getUrlParameters().get("rad"))));
  }

  @Test
  void create_doesNotAppendApiKeyUrlParamIfNoApiKeyWasFound() throws MalformedURLException {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup
    when(apiKeyManagerMock.read()).thenReturn(Optional.empty());

    // when
    TankerkoenigRequest actualRequest = TankerkoenigRequest.create(apiKeyManagerMock, geoFixture);

    // then
    assertNull(actualRequest.getUrlParameters().get("key"));
  }

  @Test
  void create_throwsOnNullApiKeyManager() {
    // when then
    assertThrows(
        NullPointerException.class,
        () -> TankerkoenigRequest.create(null, geoFixture));
  }

  @Test
  void create_throwsOnMissingDistance() {
    // given
    geoFixture.setDistance(null);

    // when then
    assertThrows(
        TankerkoenigRequest.SearchRadiusException.class,
        () -> TankerkoenigRequest.create(apiKeyManagerMock, geoFixture));
  }
}