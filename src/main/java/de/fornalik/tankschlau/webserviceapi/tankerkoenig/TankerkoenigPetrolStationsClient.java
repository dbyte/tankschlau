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

import com.google.gson.Gson;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStationsDao;
import de.fornalik.tankschlau.webserviceapi.common.GeoRequest;
import de.fornalik.tankschlau.webserviceapi.common.PetrolStationsClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Petrol stations client/DAO implementation for tankerkoenig.de response.
 *
 * @see PetrolStationsClient
 * @see PetrolStationsDao
 */
public class TankerkoenigPetrolStationsClient
    implements PetrolStationsClient<TankerkoenigResponse> {

  private final HttpClient httpClient;
  private final Gson jsonProvider;
  private final TankerkoenigJsonAdapter jsonAdapter;
  private final GeoRequest request;
  private TankerkoenigResponse response;

  /**
   * Creates a new default {@link TankerkoenigPetrolStationsClient} object for the webservice. <br>
   *
   * @param httpClient   Some {@link HttpClient} implementation.
   * @param jsonAdapter  Some json adapter implementation for petrol stations.
   * @param jsonProvider Currently fixed {@link Gson} implementation.
   * @param request      Some {@link GeoRequest} implementation.
   */
  public TankerkoenigPetrolStationsClient(
      HttpClient httpClient,
      TankerkoenigJsonAdapter jsonAdapter,
      Gson jsonProvider,
      GeoRequest request) {

    this.httpClient = httpClient;
    this.jsonAdapter = jsonAdapter;
    this.jsonProvider = jsonProvider;
    this.request = request;
    this.response = null;
  }

  @Override
  public List<PetrolStation> findAllInNeighbourhood(Geo geo) {
    this.request.setGeoUrlParameters(geo);

    // It's guaranteed by newCall(...) that returned response is not null.
    response = (TankerkoenigResponse) httpClient.newCall(
        request,
        new TankerkoenigResponse(jsonProvider));

    Objects.requireNonNull(response, "Response is null.");

    if (!response.getBody().isPresent())
      return new ArrayList<>();

    /*
    At this point we assert a valid JSON document - well formed and determined
    by the webservice's API. So all following processing should crash only if _we_
    messed things up.
    */
    return jsonAdapter.createPetrolStations(response.getBody().get());
  }

  @Override
  public TankerkoenigResponse getResponse() {
    return response;
  }

  @Override
  public String getLicenseString() {
    return response.getLicenseString();
  }

}
