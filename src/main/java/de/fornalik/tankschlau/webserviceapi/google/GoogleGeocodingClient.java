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
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import de.fornalik.tankschlau.TankSchlau;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.AddressRequest;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.StringResponse;
import de.fornalik.tankschlau.webserviceapi.common.GeocodingClient;

import java.io.IOException;
import java.util.Optional;

/**
 * Implementation of {@link GeocodingClient} for Google Geocoding webservices.
 */
public class GoogleGeocodingClient implements GeocodingClient {
  private final HttpClient httpClient;
  private final AddressRequest request;
  private TransactionInfo transactionInfo;

  public GoogleGeocodingClient() {
    this(
        TankSchlau.HTTP_CLIENT,
        GoogleGeocodingRequest.create(TankSchlau.GEOCODING_APIKEY_MANAGER));
  }

  public GoogleGeocodingClient(HttpClient httpClient, AddressRequest request) {
    this.httpClient = httpClient;
    this.request = request;
    this.transactionInfo = new TransactionInfo();
  }

  @Override
  public Optional<Geo> getGeo(Address address) throws IOException {
    request.setAddressUrlParameters(address);

    // Reset state!
    this.transactionInfo = new TransactionInfo();

    StringResponse response = (StringResponse) httpClient.newCall(request);

    if (response == null)
      throw new IOException("Response is null.");

    if (!response.getBody().isPresent())
      return Optional.empty();

    return parseJson(response.getBody().get());
  }

  @Override
  public TransactionInfo getTransactionInfo() {
    return transactionInfo;
  }

  @Override
  public String getLicenseString() {
    return "Geo data powered by Google.";
  }

  private Optional<Geo> parseJson(String s) {
    try {
      // Ride down the tree until we're reaching target data
      JsonObject root = JsonParser
          .parseString(s)
          .getAsJsonObject();

      transactionInfo.setStatus(root.get("status").getAsString());

      if (root.get("error_message") != null)
        // Element only exists when there was a server side error.
        transactionInfo.setMessage(root.get("error_message").getAsString());

      if (!"OK".equalsIgnoreCase(transactionInfo.getStatus())) {
        System.err.println("Status is NOT OK: " + transactionInfo.getStatus());
        return Optional.empty();
      }

      JsonObject geometry = root
          .getAsJsonArray("results")
          .get(0)
          .getAsJsonObject()
          .get("geometry")
          .getAsJsonObject();

      transactionInfo.setLocationType(geometry.get("location_type").getAsString());
      JsonObject location = geometry.get("location").getAsJsonObject();

      Gson gson = new Gson();
      return Optional.ofNullable(gson.fromJson(location, Geo.class));
    }

    catch (JsonParseException
        | NullPointerException
        | IllegalStateException
        | IndexOutOfBoundsException e) {
      System.err.println("Could not deserialize JSON.\n" + s + "\n");
      e.printStackTrace();

      return Optional.empty();
    }
  }
}
