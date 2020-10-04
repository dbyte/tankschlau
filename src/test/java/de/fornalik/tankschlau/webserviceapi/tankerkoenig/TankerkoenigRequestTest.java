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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * We only test for concrete implementation specific values here. All other behaviour should
 * be directly tested in unit tests for {@link de.fornalik.tankschlau.net.BaseRequest}
 */
class TankerkoenigRequestTest {
  private ApiKeyManager apiKeyManagerMock;
  private Geo geoMock;

  @BeforeEach
  void setUp() {
    this.apiKeyManagerMock = mock(ApiKeyManager.class);
    when(apiKeyManagerMock.read()).thenReturn(Optional.of("000-abc-def-111"));

    this.geoMock = mock(Geo.class);
    when(geoMock.getLatitude()).thenReturn(53.1234);
    when(geoMock.getLongitude()).thenReturn(48.5678);
    when(geoMock.getDistance()).thenReturn(Optional.of(12.0));
  }

  @Test
  void create_withGeo_constructsProperValues() throws MalformedURLException {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup

    // when
    TankerkoenigRequest actualRequest = TankerkoenigRequest.create(apiKeyManagerMock, geoMock);

    // then

    // Assert proper common values, independent of Geo values.
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
  void create_withoutGeo_constructsProperValues() throws MalformedURLException {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup

    // when
    TankerkoenigRequest actualRequest = TankerkoenigRequest.create(apiKeyManagerMock);

    // then

    // Assert proper common values, independent of Geo values.
    this.assert_create_constructsProperValues();

    // Assert proper Geo values.
    assertNull(actualRequest.getUrlParameters().get("lat"));
    assertNull(actualRequest.getUrlParameters().get("lng"));
    assertNull(actualRequest.getUrlParameters().get("rad"));
  }

  private void assert_create_constructsProperValues() throws MalformedURLException {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup

    // when
    TankerkoenigRequest actualRequest = TankerkoenigRequest.create(apiKeyManagerMock);

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
  void create_doesNotAppendApiKeyUrlParamIfNoApiKeyWasFound() throws MalformedURLException {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup
    when(apiKeyManagerMock.read()).thenReturn(Optional.empty());

    // when
    TankerkoenigRequest actualRequest = TankerkoenigRequest.create(apiKeyManagerMock, geoMock);

    // then
    assertNull(actualRequest.getUrlParameters().get("apikey"));
  }

  @Test
  void create_throwsOnNullApiKeyManager() {
    // when then
    assertThrows(
        NullPointerException.class,
        () -> TankerkoenigRequest.create(null, geoMock));
  }

  @Test
  void create_throwsOnMissingDistance() {
    // given
    when(geoMock.getDistance()).thenReturn(Optional.empty());

    // when then
    assertThrows(
        TankerkoenigRequest.SearchRadiusException.class,
        () -> TankerkoenigRequest.create(apiKeyManagerMock, geoMock));
  }

  @Test
  void setGeo_doesSetUrlParametersProperly() throws MalformedURLException {
    // given
    TankerkoenigRequest actualRequest = TankerkoenigRequest.create(apiKeyManagerMock);

    // when
    actualRequest.setGeo(geoMock);

    // then
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
  void setGeo_throwsImmediatelyOnNullArgument() throws MalformedURLException {
    // given
    TankerkoenigRequest actualRequest = TankerkoenigRequest.create(apiKeyManagerMock);

    // when then
    assertThrows(NullPointerException.class, () -> actualRequest.setGeo(null));
  }
}