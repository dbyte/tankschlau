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
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.common.GeoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * We only test for concrete implementation specific values here. All other behaviour should
 * be directly tested in unit tests for {@link de.fornalik.tankschlau.net.BaseRequest}
 */
class TankerkoenigRequestTest {
  private GeoRequest actualRequest;
  private ApiKeyManager apiKeyManagerMock;
  private Geo geoMock;

  @BeforeEach
  void setUp() {
    this.actualRequest = null;

    this.apiKeyManagerMock = mock(ApiKeyManager.class);
    when(apiKeyManagerMock.read()).thenReturn(Optional.of("000-abc-def-111"));

    this.geoMock = mock(Geo.class);
    when(geoMock.getLatitude()).thenReturn(53.1234);
    when(geoMock.getLongitude()).thenReturn(48.5678);
    when(geoMock.getDistance()).thenReturn(Optional.of(12.0));
  }

  @Test
  void create_constructsProperValues() {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup

    // when
    actualRequest = TankerkoenigRequest.create(apiKeyManagerMock);

    // then

    // Assert proper common values.
    this.assert_create_constructsProperValues();

    // Assert that there are no Geo values after construction.
    assertNull(actualRequest.getUrlParameters().get("lat"));
    assertNull(actualRequest.getUrlParameters().get("lng"));
    assertNull(actualRequest.getUrlParameters().get("rad"));
  }

  private void assert_create_constructsProperValues() {
    // Precondition: Check if test is set up.
    assert apiKeyManagerMock.read().isPresent();

    // Precondition: actualRequest must be set up before at call side.
    assert actualRequest != null;

    // then
    assertEquals(
        "https://creativecommons.tankerkoenig.de/json/list.php",
        actualRequest.getBaseUrl().toString());

    assertEquals(Request.HttpMethod.GET, actualRequest.getHttpMethod());

    assertEquals("application/json; charset=utf-8", actualRequest.getHeaders().get("Accept"));

    assertEquals("dist", actualRequest.getUrlParameters().get("sort"));
    assertEquals("all", actualRequest.getUrlParameters().get("type"));
    assertEquals(apiKeyManagerMock.read().get(), actualRequest.getUrlParameters().get("apikey"));
  }

  @Test
  void create_doesNotAppendApiKeyUrlParamIfNoApiKeyWasFound() {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup
    when(apiKeyManagerMock.read()).thenReturn(Optional.empty());

    // when
    actualRequest = TankerkoenigRequest.create(apiKeyManagerMock);

    // then
    assertNull(actualRequest.getUrlParameters().get("apikey"));
  }

  @Test
  void create_throwsOnNullApiKeyManager() {
    // when then
    assertThrows(
        NullPointerException.class,
        () -> TankerkoenigRequest.create(null));
  }

  @Test
  void setGeoUrlParameters_doesSetUrlParametersProperly() {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup
    actualRequest = TankerkoenigRequest.create(apiKeyManagerMock);

    // when
    actualRequest.setGeoUrlParameters(geoMock);

    // then

    // Assert that proper common values are left untouched after geo computation.
    this.assert_create_constructsProperValues();

    // Assert proper Geo values.
    assertEquals(
        geoMock.getLatitude(),
        Double.valueOf(actualRequest.getUrlParameters().get("lat")));

    assertEquals(
        geoMock.getLongitude(),
        Double.valueOf(actualRequest.getUrlParameters().get("lng")));

    assertEquals(
        geoMock.getDistance(),
        Optional.of(Double.valueOf(actualRequest.getUrlParameters().get("rad"))));
  }

  @Test
  void setGeoUrlParameters_throwsOnMissingDistance() {
    // given
    actualRequest = TankerkoenigRequest.create(apiKeyManagerMock);
    when(geoMock.getDistance()).thenReturn(Optional.empty());

    // when then
    assertThrows(
        TankerkoenigRequest.SearchRadiusException.class,
        () -> actualRequest.setGeoUrlParameters(geoMock));
  }

  @Test
  void setGeoUrlParameters_throwsImmediatelyOnNullArgument() {
    // given
    actualRequest = TankerkoenigRequest.create(apiKeyManagerMock);

    // when then
    assertThrows(NullPointerException.class, () -> actualRequest.setGeoUrlParameters(null));
  }
}