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
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.net.StringResponse;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Optional;

// TODO unit tests

public class GoogleGeocodingClient {
  private final HttpClient httpClient;
  private final Request request;

  public GoogleGeocodingClient() throws MalformedURLException {
    this(
        TankSchlau.HTTP_CLIENT,
        GeocodingRequest.create(TankSchlau.GEOCODING_APIKEY_MANAGER));
  }

  public GoogleGeocodingClient(HttpClient httpClient, Request request) {
    this.httpClient = httpClient;
    this.request = request;
  }

  public Optional<Geo> getGeo(Address address) throws IOException {
    ((GeocodingRequest) request).setAddressUrlParameters(address);

    StringResponse response = (StringResponse) httpClient.newCall(request);

    if (response == null)
      throw new IOException("Response is null.");

    if (!response.getBody().isPresent())
      return Optional.empty();

    return parseJson(response.getBody().get());
  }

  private Optional<Geo> parseJson(String s) {
    try {
      // Ride down the tree until we're reaching target data
      JsonObject geometry = JsonParser
          .parseString(s)
          .getAsJsonObject()
          .getAsJsonArray("results")
          .get(0)
          .getAsJsonObject()
          .get("geometry")
          .getAsJsonObject();

      JsonObject location = geometry.get("location").getAsJsonObject();
      String locationType = geometry.get("location_type").getAsString();

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
