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

package de.fornalik.tankschlau.webserviceapi.google;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.webserviceapi.ApiKeyManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * We only test for concrete implementation specific values here. All other behaviour should
 * be directly tested in unit tests for {@link de.fornalik.tankschlau.net.BaseRequest}
 */
class GeocodingRequestTest {
  private ApiKeyManager apiKeyManagerMock;
  private Address addressMock;

  @BeforeEach
  void setUp() {
    this.apiKeyManagerMock = Mockito.mock(ApiKeyManager.class);
    when(apiKeyManagerMock.read()).thenReturn(Optional.of("222-hij-klm-333"));

    this.addressMock = Mockito.mock(Address.class);
    when(addressMock.getStreet()).thenReturn("An den Äckern");
    when(addressMock.getHouseNumber()).thenReturn("2");
    when(addressMock.getPostCode()).thenReturn("38446");
    when(addressMock.getCity()).thenReturn("Wolfsburg");
  }

  @Test
  void create_constructsProperValues() throws MalformedURLException {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup

    // when
    GeocodingRequest actualRequest = GeocodingRequest.create(apiKeyManagerMock, addressMock);

    // then
    assertEquals(
        "https://maps.googleapis.com/maps/api/geocode/json?",
        actualRequest.getBaseUrl().toString());

    assertEquals(Request.HttpMethod.GET, actualRequest.getHttpMethod());

    assertEquals("application/json; charset=utf-8", actualRequest.getHeaders().get("Accept"));

    assertEquals("de", actualRequest.getUrlParameters().get("region"));

    assertEquals(
        "An+den+%C3%84ckern+2+38446+Wolfsburg",
        actualRequest.getUrlParameters().get("address"));

    assertEquals(apiKeyManagerMock.read().get(), actualRequest.getUrlParameters().get("key"));
  }

  /*@Test
  void create_XXX() throws IOException {
    // given
    final HttpClient HTTP_CLIENT = new OkHttpClient();

    // when
    GeocodingRequest actualRequest = GeocodingRequest.create(apiKeyManagerMock, addressMock);
    Response response = HTTP_CLIENT.newCall(actualRequest);
    System.out.println(response.getBody().get());
  }*/
}