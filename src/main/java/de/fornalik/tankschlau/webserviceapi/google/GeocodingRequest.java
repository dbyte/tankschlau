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
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.BaseRequest;
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.webserviceapi.ApiKeyManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Implementation of {@link BaseRequest} for Google Geocoding web service.
 */
public class GeocodingRequest extends BaseRequest {
  private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json?";
  private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
  private static final String ACCEPT_JSON = "application/json; charset=utf-8";
  private ApiKeyManager apiKeyManager;
  private Address address;

  private GeocodingRequest() {}

  /**
   * Factory method, creates a new HTTP request object for Google Geocoding web service.
   *
   * @param address The user's address data. Its empty {@link Geo} object gets filled with the
   *                resulting latitude & longitude by calling the service.
   * @return {@link Address} object as in, ready for use which within a {@link Request}.
   * @throws MalformedURLException If the base URL is invalid.
   */
  public static GeocodingRequest create(ApiKeyManager apiKeyManager, Address address)
  throws MalformedURLException {
    GeocodingRequest instance = new GeocodingRequest();

    instance.address = Objects.requireNonNull(
        address,
        "Address must not be null.");

    instance.apiKeyManager = Objects.requireNonNull(
        apiKeyManager,
        "apiKeyManager must not be null.");

    instance.setBaseData();
    instance.setUrlParameters();

    return instance;
  }

  private void setBaseData() throws MalformedURLException {
    setBaseUrl(new URL(BASE_URL));
    setHttpMethod(HTTP_METHOD);
    addHeader("Accept", ACCEPT_JSON);
  }

  private void setUrlParameters() {
    addUrlParameter("region", "de");

    addUrlParameter("address", processAddressQueryPart1());
    appendUrlString("address", ",+", null);
    appendUrlString("address", processAddressQueryPart2(), "UTF-8");

    /* Only add API key if we got one. Google will inform us about a missing/invalid key
    in its response, where we handle errors anyway. */
    apiKeyManager.read().ifPresent(value -> addUrlParameter("key", value));
  }

  private String processAddressQueryPart1() {
    return (address.getStreet() + " " + address.getHouseNumber()).trim();
  }

  private String processAddressQueryPart2() {
    return (address.getPostCode() + " " + address.getCity()).trim();
  }

}
