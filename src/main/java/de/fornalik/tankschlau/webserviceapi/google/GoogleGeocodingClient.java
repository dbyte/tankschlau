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
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.JsonResponse;
import de.fornalik.tankschlau.net.Response;
import de.fornalik.tankschlau.storage.TransactInfo;
import de.fornalik.tankschlau.webserviceapi.common.AddressRequest;
import de.fornalik.tankschlau.webserviceapi.common.GeocodingService;

import java.util.Objects;
import java.util.Optional;

/**
 * Implementation of {@link GeocodingService} for Google Geocoding webservices.
 *
 * @see
 * <a href="https://developers.google.com/maps/documentation/geocoding/overview#GeocodingResponses">Google documentation: GeocodingResponses</a>
 */
public class GoogleGeocodingClient implements GeocodingService {
  private final HttpClient httpClient;
  private final AddressRequest request;
  private final Response response;

  /**
   * Constructor
   *
   * @param httpClient Some implementation of {@link HttpClient} for interaction with webservice.
   * @param request    Some implementation of {@link AddressRequest}, forming a concrete request.
   * @param response   Some implementation of {@link Response}. Will be implicitly recycled.
   */
  public GoogleGeocodingClient(
      HttpClient httpClient,
      AddressRequest request,
      Response response) {

    this.httpClient = Objects.requireNonNull(httpClient);
    this.request = Objects.requireNonNull(request);
    this.response = Objects.requireNonNull(response);
  }

  @Override
  public Optional<Geo> findGeo(Address forAddress) {
    request.setAddressUrlParameters(Objects.requireNonNull(forAddress));
    response.reset();

    // It's guaranteed by newCall(...) that returned response is not null.
    httpClient.newCall(request, response, String.class);

    /*
    Note: After newCall, the field response.transactInfo may already contain error message etc,
    mutated by the http client while processing communication/request/response.
    */

    Objects.requireNonNull(response, "Response is null.");

    if (response.getBody() == null)
      return Optional.empty();

    /*
    At this point we assert a valid JSON document - well formed and determined
    by the webservice's API. So all following processing should crash only if _we_
    messed things up.
    */
    // Get body data of server response.
    String jsonString = response.getBody().getData(String.class);

    return ((JsonResponse) response).fromJson(jsonString, Geo.class);
  }

  @Override
  public TransactInfo getTransactInfo() {
    return response.getTransactInfo();
  }
}
