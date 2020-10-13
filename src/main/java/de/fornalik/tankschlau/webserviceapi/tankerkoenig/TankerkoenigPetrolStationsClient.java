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
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.JsonResponse;
import de.fornalik.tankschlau.net.Response;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.storage.PetrolStationsService;
import de.fornalik.tankschlau.storage.TransactInfo;
import de.fornalik.tankschlau.webserviceapi.common.GeoRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Petrol stations client/service implementation for tankerkoenig.de response.
 *
 * @see PetrolStation
 */
public class TankerkoenigPetrolStationsClient implements PetrolStationsService {

  private final HttpClient httpClient;
  private final TankerkoenigJsonAdapter jsonAdapter;
  private final GeoRequest request;
  private JsonResponse response;

  /**
   * Creates a new default {@link TankerkoenigPetrolStationsClient} object for the webservice. <br>
   *
   * @param httpClient  Some {@link HttpClient} implementation.
   * @param jsonAdapter Some json adapter implementation for petrol stations.
   * @param request     Some {@link GeoRequest} implementation.
   * @param response    Some initialized implementation of a {@link Response} object.
   */
  public TankerkoenigPetrolStationsClient(
      HttpClient httpClient,
      TankerkoenigJsonAdapter jsonAdapter,
      GeoRequest request,
      JsonResponse response) {

    this.httpClient = httpClient;
    this.jsonAdapter = jsonAdapter;
    this.request = request;
    this.response = response;
  }

  @Override
  public List<PetrolStation> findAllInNeighbourhood(Geo geo) {
    request.setGeoUrlParameters(geo);
    response.reset();

    // It's guaranteed by newCall(...) that returned response is not null.
    response = (JsonResponse) httpClient.newCall(request, response, String.class);
    // Note: After newCall, the field response.transactInfo may already contain error message etc,
    // mutated by the http client while processing communication/request/response.

    Objects.requireNonNull(response, "Response is null.");

    if (!response.getBody().isPresent())
      return new ArrayList<>();

    // Get body data from server response.
    String jsonString = response.getBody().get().getData(String.class);

    // Deserialize data from JSON data which are of informal type -
    // like status, licence string, error message because of wrong API key etc.
    // Note: Response objects will be mutated there. No need to retrieve any results here.
    response.fromJson(jsonString, TankerkoenigResponse.ResponseDto.class);

    /*
    At this point we assert a valid JSON document - well formed and determined
    by the webservice's API. So all following processing should crash only if _we_
    messed things up.
    */
    return jsonAdapter.createPetrolStations(jsonString);
  }

  @Override
  public TransactInfo getTransactInfo() {
    return this.response.getTransactInfo();
  }
}
