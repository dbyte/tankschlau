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
import de.fornalik.tankschlau.net.AddressRequest;
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.util.StringLegalizer;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;

import java.util.Objects;

/**
 * Implementation of {@link AddressRequest} for Google Geocoding web service.
 */
public class GoogleGeocodingRequest extends AddressRequest {
  private static final String BASE_URL = "https://maps.googleapis.com/maps/api/geocode/json";
  private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
  private static final String ACCEPT_JSON = "application/json; charset=utf-8";
  private ApiKeyManager apiKeyManager;

  private GoogleGeocodingRequest() {}

  /**
   * Factory method, creates a new HTTP request object for Google Geocoding web service.
   *
   * @return {@link Address} object as in, ready for use which within a {@link Request}.
   */
  public static GoogleGeocodingRequest create(ApiKeyManager apiKeyManager) {
    GoogleGeocodingRequest instance = new GoogleGeocodingRequest();

    instance.apiKeyManager = Objects.requireNonNull(
        apiKeyManager,
        "apiKeyManager must not be null.");

    instance.setBaseData();
    instance.setCommonUrlParameters();

    return instance;
  }

  @Override
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

  private void setBaseData() {
    setBaseUrl(StringLegalizer.create(BASE_URL).mandatory().toUrl());
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
