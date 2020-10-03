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
import de.fornalik.tankschlau.net.BaseRequest;
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.webserviceapi.ApiKeyManager;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

/**
 * Implementation of {@link BaseRequest} for web service Tankerkoenig.de
 */
public class TankerkoenigRequest extends BaseRequest {
  private static final String BASE_URL = "https://creativecommons.tankerkoenig.de/json/list.php";
  private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
  private static final String ACCEPT_JSON = "application/json; charset=utf-8";
  private ApiKeyManager apiKeyManager;
  private Geo geo;

  private TankerkoenigRequest() {}

  /**
   * Creates a new default HTTP request object for web service Tankerkoenig.de. Use this
   * if you don't have {@link Geo} data available yet and set them later.
   *
   * @param apiKeyManager Service which controls handling of the web service api key.
   * @return A new default {@link TankerkoenigRequest} instance.
   * @throws MalformedURLException If the base URL is invalid.
   * @see TankerkoenigRequest#create(ApiKeyManager, Geo)
   */
  public static TankerkoenigRequest create(ApiKeyManager apiKeyManager)
  throws MalformedURLException {
    return TankerkoenigRequest.create(apiKeyManager, null);
  }

  /**
   * Creates a new HTTP request object for web service Tankerkoenig.de to get
   * petrol stations in the neighbourhood around the given geographical data.
   *
   * @param apiKeyManager Service which controls handling of the web service api key.
   * @param geo           The user's geographical data and maximum search radius im km to search
   *                      for petrol stations in the neighbourhood.
   * @return A new {@link TankerkoenigRequest} instance, ready for use within a {@link Request}.
   * @throws MalformedURLException If the base URL is invalid.
   * @throws SearchRadiusException If distance value of {@link Geo} data is missing.
   * @see TankerkoenigRequest#create(ApiKeyManager)
   */
  public static TankerkoenigRequest create(ApiKeyManager apiKeyManager, Geo geo)
  throws MalformedURLException {
    TankerkoenigRequest instance = new TankerkoenigRequest();

    instance.geo = geo; // nullable

    instance.apiKeyManager = Objects.requireNonNull(
        apiKeyManager,
        "apiKeyManager must not be null.");

    instance.setBaseData();
    instance.setUrlParameters();

    return instance;
  }

  /**
   * Sets or overwrites the geographical URL parameters for this request.
   *
   * @param geo {@link Geo} data, must not be null.
   * @throws NullPointerException If given geo data is null.
   */
  public void setGeo(Geo geo) {
    this.geo = Objects.requireNonNull(geo, "Geo must not be null.");
    this.setGeoUrlParameters();
  }

  private void setBaseData() throws MalformedURLException {
    setBaseUrl(new URL(BASE_URL));
    setHttpMethod(HTTP_METHOD);
    addHeader("Accept", ACCEPT_JSON);
  }

  private void setUrlParameters() {
    if (geo != null)
      setGeoUrlParameters();

    /* As we sort data ourselves, always request "all" petrol types. Per web service API definition
    of Tankerkoenig.de, "sort" must be set to "dist" when requesting "type" with "all". */
    addUrlParameter("sort", "dist");
    addUrlParameter("type", "all");

    /* Only add API key if we got one. Tankerkoenig will inform us about a missing/invalid key
    in its response, where we handle errors anyway. */
    apiKeyManager.read().ifPresent(value -> addUrlParameter("apikey", value));
  }

  private void setGeoUrlParameters() {
    Double maxSearchRadius = geo
        .getDistance()
        .orElseThrow(SearchRadiusException::new);

    addUrlParameter("lat", Double.valueOf(geo.getLatitude()).toString());
    addUrlParameter("lng", Double.valueOf(geo.getLongitude()).toString());
    addUrlParameter("rad", maxSearchRadius.toString());
  }

  public static class SearchRadiusException extends IllegalStateException {
    protected SearchRadiusException() {
      super("Maximum distance radius (km), used for searching petrol stations "
                + "in the user's neighbourhood, must not be null.");
    }
  }
}
