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
import de.fornalik.tankschlau.util.StringLegalizer;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.common.GeoRequest;

import java.util.Objects;

/**
 * Implementation of {@link GeoRequest} for web service tankerkoenig.de
 */
public class TankerkoenigRequest extends BaseRequest implements GeoRequest {
  private static final String BASE_URL = "https://creativecommons.tankerkoenig.de/json/list.php";
  private static final HttpMethod HTTP_METHOD = HttpMethod.GET;
  private static final String ACCEPT_JSON = "application/json; charset=utf-8";
  private ApiKeyManager apiKeyManager;

  private TankerkoenigRequest() {}

  /**
   * Creates a new HTTP request object for web service Tankerkoenig.de to get
   * petrol stations in the neighbourhood around the given geographical data.
   *
   * @param apiKeyManager Service which controls handling of the web service api key.
   * @return A new {@link TankerkoenigRequest} instance.
   * @throws SearchRadiusException If distance value of {@link Geo} data is missing.
   */
  public static GeoRequest create(ApiKeyManager apiKeyManager) {
    TankerkoenigRequest instance = new TankerkoenigRequest();

    instance.apiKeyManager = Objects.requireNonNull(
        apiKeyManager,
        "apiKeyManager must not be null.");

    instance.setBaseData();
    instance.setCommonUrlParameters();

    return instance;
  }

  @Override
  public void setGeoUrlParameters(Geo geo) {
    Objects.requireNonNull(geo, "Geo must not be null.");

    Double maxSearchRadius = geo
        .getDistance()
        .orElseThrow(SearchRadiusException::new);

    putUrlParameter("lat", Double.valueOf(geo.getLatitude()).toString());
    putUrlParameter("lng", Double.valueOf(geo.getLongitude()).toString());
    putUrlParameter("rad", maxSearchRadius.toString());
  }

  private void setBaseData() {
    setBaseUrl(StringLegalizer.create(BASE_URL).mandatory().toUrl());
    setHttpMethod(HTTP_METHOD);
    putHeader("Accept", ACCEPT_JSON);
  }

  private void setCommonUrlParameters() {
    /* As we sort data ourselves, always request "all" petrol types. Per web service API definition
    of Tankerkoenig.de, "sort" must be set to "dist" when requesting "type" with "all". */
    putUrlParameter("sort", "dist");
    putUrlParameter("type", "all");

    /* Only add API key if we got one. Tankerkoenig will inform us about a missing/invalid key
    in its response, where we handle errors anyway. */
    apiKeyManager.read().ifPresent(value -> putUrlParameter("apikey", value));
  }

  public static class SearchRadiusException extends IllegalStateException {
    protected SearchRadiusException() {
      super("Maximum distance radius (km), used for searching petrol stations "
                + "in the user's neighbourhood, must not be null.");
    }
  }
}
