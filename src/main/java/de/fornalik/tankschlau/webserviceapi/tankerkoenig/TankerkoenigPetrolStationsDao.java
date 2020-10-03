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
import com.google.gson.TypeAdapter;
import de.fornalik.tankschlau.TankSchlau;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.StringResponse;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStations;
import de.fornalik.tankschlau.station.PetrolStationsDao;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Petrol stations DAO implementation for tankerkoenig.de response.
 */
public class TankerkoenigPetrolStationsDao implements PetrolStationsDao {
  private final HttpClient httpClient;
  private final TankerkoenigRequest request;
  private final TypeAdapter<?> gsonAdapter;
  private TransactionInfo transactionInfo;

  public TankerkoenigPetrolStationsDao() throws MalformedURLException {
    this(
        TankSchlau.HTTP_CLIENT,
        TankSchlau.PETROL_STATIONS_JSON_ADAPTER,
        TankerkoenigRequest.create(TankSchlau.TANKERKOENIG_APIKEY_MANAGER));
  }

  public TankerkoenigPetrolStationsDao(
      HttpClient httpClient,
      TypeAdapter<?> gsonAdapter,
      TankerkoenigRequest request) {

    this.httpClient = httpClient;
    this.request = request;
    this.gsonAdapter = gsonAdapter;
    this.transactionInfo = new TransactionInfo();
  }

  @Override
  public List<PetrolStation> getAllInNeighbourhood(Geo geo) throws IOException {
    this.request.setGeo(geo);

    StringResponse response = (StringResponse) httpClient.newCall(request);
    String body = response.getBody().orElse("");

    if ("".equals(body))
      return new ArrayList<>();

    this.transactionInfo = new Gson().fromJson(body, TransactionInfo.class);

    return PetrolStations.createFromJson(body, gsonAdapter);
  }

  @Override
  public TransactionInfo getTransactionInfo() {
    return transactionInfo;
  }

/*  @SuppressWarnings("unused")
  public static class TransactionInfo {
    @SerializedName("ok") private boolean ok;
    @SerializedName("license") private String license;
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;

    public boolean isOk() {
      return ok;
    }

    public String getLicense() {
      return nullToEmpty(license);
    }

    public String getStatus() {
      return nullToEmpty(status);
    }

    public String getMessage() {
      return nullToEmpty(message);
    }

    private String nullToEmpty(String s) {
      return StringLegalizer.create(s).nullToEmpty().end();
    }
  }*/
}
