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

import de.fornalik.tankschlau.TankSchlau;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.GeoRequest;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.StringResponse;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStationsDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Petrol stations DAO implementation for tankerkoenig.de response.
 *
 * @see PetrolStationsDao
 */
public class TankerkoenigPetrolStationsDao implements PetrolStationsDao {
  private final HttpClient httpClient;
  private final GeoRequest request;
  private final TankerkoenigJsonAdapter jsonAdapter;
  private TransactionInfo transactionInfo;

  /**
   * Creates a new default {@link TankerkoenigPetrolStationsDao} object for the webservice. <br>
   * <span style="color:yellow;">Use this constructor in production.</span><br><br>
   * Implicitly uses the app's static {@link TankSchlau#HTTP_CLIENT},
   * {@link TankSchlau#PETROL_STATIONS_JSON_ADAPTER} and
   * {@link TankSchlau#TANKERKOENIG_APIKEY_MANAGER}. <br>
   *
   * @see #TankerkoenigPetrolStationsDao(HttpClient, TankerkoenigJsonAdapter, GeoRequest)
   */
  public TankerkoenigPetrolStationsDao() {
    this(
        TankSchlau.HTTP_CLIENT,
        TankSchlau.PETROL_STATIONS_JSON_ADAPTER,
        TankerkoenigRequest.create(TankSchlau.TANKERKOENIG_APIKEY_MANAGER));
  }

  /**
   * Creates a new default {@link TankerkoenigPetrolStationsDao} object for the webservice. <br>
   * Dependency Injection variant of {@link #TankerkoenigPetrolStationsDao()}. <br>
   * <span style="color:yellow;">You should use this constructor in tests only.</span><br><br>
   *
   * @param httpClient  Some {@link HttpClient} implementation.
   * @param jsonAdapter Some json adapter implementation for petrol stations.
   * @param request     Some {@link GeoRequest} implementation.
   * @see #TankerkoenigPetrolStationsDao()
   */
  public TankerkoenigPetrolStationsDao(
      HttpClient httpClient,
      TankerkoenigJsonAdapter jsonAdapter,
      GeoRequest request) {

    this.httpClient = httpClient;
    this.request = request;
    this.jsonAdapter = jsonAdapter;
    this.transactionInfo = new TransactionInfo();
  }

  @Override
  public List<PetrolStation> getAllInNeighbourhood(Geo geo) throws IOException {
    this.request.setGeoUrlParameters(geo);

    // Reset state!
    this.transactionInfo = new TransactionInfo();

    StringResponse response = (StringResponse) httpClient.newCall(request);

    if (response == null) {
      this.transactionInfo.setOk(false);
      this.transactionInfo.setStatus(getClass().getSimpleName() + "_RESPONSE_NULL");
      this.transactionInfo.setMessage("Response is null.");
      return new ArrayList<>();
    }

    String body = response.getBody().orElse("");

    if ("".equals(body)) {
      this.transactionInfo.setOk(false);
      this.transactionInfo.setStatus(getClass().getSimpleName() + "_EMPTY_RESPONSE_BODY");
      this.transactionInfo.setMessage("Response body is empty.");
      return new ArrayList<>();
    }

    /* At this point we assert a valid JSON document - well formed and determined
    by the webservice's API. So all following processing should crash only if _we_
    messed things up. */

    this.transactionInfo = TankSchlau.JSON_PROVIDER.fromJson(body, TransactionInfo.class);

    if (!transactionInfo.isOk()) {
      System.err.println("Log.Error: " + transactionInfo.getStatus());
      System.err.println("Log.Error: " + transactionInfo.getMessage());
      return new ArrayList<>();
    }

    return jsonAdapter.createPetrolStations(body);
  }

  @Override
  public TransactionInfo getTransactionInfo() {
    return transactionInfo;
  }
}
