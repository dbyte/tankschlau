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
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Implementation of {@link BaseRequest} for Google Geocoding web service.
 */
public class GeocodingRequest extends BaseRequest {
  private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
  private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
  private static final String ACCEPT_JSON = "application/json; charset=utf-8";
  private ApiKeyManager apiKeyManager;

  private GeocodingRequest() {}

  /**
   * Factory method, creates a new HTTP request object for Google Geocoding web service.
   *
   * @return {@link Address} object as in, ready for use which within a {@link Request}.
   * @throws MalformedURLException If the base URL is invalid.
   */
  public static GeocodingRequest create(ApiKeyManager apiKeyManager)
  throws MalformedURLException {
    GeocodingRequest instance = new GeocodingRequest();

    instance.apiKeyManager = Objects.requireNonNull(
        apiKeyManager,
        "apiKeyManager must not be null.");

    instance.setBaseData();
    instance.setCommonUrlParameters();

    return instance;
  }

  /**
   * Sets or overwrites the address URL parameters for this request.
   *
   * @param address The user's address data. Its empty {@link Geo} object gets filled with the
   *                resulting latitude & longitude by calling the service.
   * @throws NullPointerException If given address is null.
   */
  public void setAddressUrlParameters(Address address) {
    Objects.requireNonNull(address, "Address must not be null.");

    addUrlParameter(
        "address",
        (address.getStreet() + " " + address.getHouseNumber()).trim());

    appendUrlString(
        "address",
        ",+",
        null);

    appendUrlString(
        "address",
        (address.getPostCode() + " " + address.getCity()).trim(),
        "UTF-8");
  }

  private void setBaseData() throws MalformedURLException {
    setBaseUrl(new URL(BASE_URL));
    setHttpMethod(HTTP_METHOD);
    addHeader("Accept", ACCEPT_JSON);
    addHeader("Accept-Language", "de");
  }

  private void setCommonUrlParameters() {
    addUrlParameter("region", "de");

    /* Only add API key if we got one. Google will inform us about a missing/invalid key
    in its response, where we handle errors anyway. */
    apiKeyManager.read().ifPresent(value -> addUrlParameter("key", value));
  }
}
