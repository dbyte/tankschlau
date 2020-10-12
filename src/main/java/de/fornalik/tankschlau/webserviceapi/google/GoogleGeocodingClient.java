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

import com.google.gson.Gson;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.webserviceapi.common.AddressRequest;
import de.fornalik.tankschlau.webserviceapi.common.GeocodingClient;

import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@link GeocodingClient} for Google Geocoding webservices.
 *
 * @see
 * <a href="https://developers.google.com/maps/documentation/geocoding/overview#GeocodingResponses">Google documentation: GeocodingResponses</a>
 */
public class GoogleGeocodingClient implements GeocodingClient<GoogleGeocodingResponse> {
  private final HttpClient httpClient;
  private final Gson jsonProvider;
  private final AddressRequest request;
  private GoogleGeocodingResponse response;

  /**
   * Constructor
   *
   * @param httpClient Some implementation of {@link HttpClient} for interaction with webservice.
   * @param request    Some implementation of {@link AddressRequest}, forming a concrete request.
   */
  public GoogleGeocodingClient(HttpClient httpClient, Gson jsonProvider, AddressRequest request) {
    this.httpClient = Objects.requireNonNull(httpClient);
    this.jsonProvider = Objects.requireNonNull(jsonProvider);
    this.request = Objects.requireNonNull(request);
  }

  @Override
  public Optional<Geo> getGeo(Address address) {
    request.setAddressUrlParameters(address);

    response = (GoogleGeocodingResponse) httpClient.newCall(
        request,
        new GoogleGeocodingResponse(jsonProvider));

    Objects.requireNonNull(response, "Response is null.");

    // Return populated Geo object if body is present, else return Optional.empty
    return response.getBody().flatMap(jsonString -> response.fromJson(jsonString));
  }

  @Override
  public GoogleGeocodingResponse getResponse() {
    return response;
  }

  @Override
  public String getLicenseString() {
    return "Geo data powered by Google.";
  }
}
