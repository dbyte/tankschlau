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

import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.TankSchlau;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.AddressRequest;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.StringResponse;
import de.fornalik.tankschlau.webserviceapi.common.GeocodingClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Implementation of {@link GeocodingClient} for Google Geocoding webservices.
 *
 * @see
 * <a href="https://developers.google.com/maps/documentation/geocoding/overview#GeocodingResponses">Google documentation: GeocodingResponses</a>
 */
public class GoogleGeocodingClient implements GeocodingClient {
  private final HttpClient httpClient;
  private final AddressRequest request;
  private TransactionInfo transactionInfo;

  /**
   * <span style="color:yellow;">Use this constructor for production</span> as it implicitly
   * uses predefined static dependencies defined at startup.
   *
   * @see #GoogleGeocodingClient(HttpClient, AddressRequest)
   */
  public GoogleGeocodingClient() {
    this(
        TankSchlau.HTTP_CLIENT,
        GoogleGeocodingRequest.create(TankSchlau.GEOCODING_APIKEY_MANAGER));
  }

  /**
   * Dependency injection variant of {@link #GoogleGeocodingClient()},
   * <span style="color:yellow;">e.g. for testing purposes.</span>
   *
   * @param httpClient Some implementation of {@link HttpClient} for interaction with webservice.
   * @param request    Some implementation of {@link AddressRequest}, forming a concrete request.
   * @see #GoogleGeocodingClient()
   */
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

    if (response == null) {
      transactionInfo.setStatus(StringResponse.class.getSimpleName() + "_NULL");
      transactionInfo.setMessage("Response is null.");
      return Optional.empty();
    }

    if (!response.getBody().isPresent()) {
      transactionInfo.setStatus(StringResponse.class.getSimpleName() + "_EMPTY_BODY");
      transactionInfo.setMessage("Response body is empty.");
      return Optional.empty();
    }

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

  private Optional<Geo> parseJson(String in) {
    ResponseDTO dto = TankSchlau.JSON_PROVIDER.fromJson(in, ResponseDTO.class);

    if (dto == null) {
      transactionInfo.setStatus(ResponseDTO.class.getSimpleName() + "_NULL");
      transactionInfo.setMessage("JSON string could not be converted. String is: " + in);
      return Optional.empty();
    }

    Optional<Geo> geo = Optional.empty();

    if (dto.results != null && dto.results.size() > 0) {
      /* From here, we can trust that webservice has set values for latitude, longitude
      and location type. */
      ResultDTO firstResult = dto.results.get(0);
      transactionInfo.setLocationType(firstResult.getLocationType());
      geo = Optional.of(firstResult.getAsGeo());
    }

    // From here, we can trust that webservice has set values for status & message.
    transactionInfo.setStatus(dto.status);
    transactionInfo.setMessage(dto.message);

    return geo;
  }

  /**
   * Object relational mapper for Gson. It must correlate with the root level json object
   * of the Google Geocoding response.
   */
  @SuppressWarnings({"unused", "FieldMayBeFinal", "MismatchedQueryAndUpdateOfCollection"})
  private static class ResponseDTO {
    @SerializedName("status") private String status;
    @SerializedName("error_message") private String message;
    @SerializedName("results") private ArrayList<ResultDTO> results;

    private ResponseDTO() {
      results = new ArrayList<>();
    }
  }

  /**
   * Object relational mapper for Gson. It represents the json array "results" of the
   * Google Geocoding response.
   */
  @SuppressWarnings("unused")
  private static class ResultDTO {
    @SerializedName("geometry") private Geometry geometry;

    private Geo getAsGeo() {
      return new Geo(geometry.location.latitude, geometry.location.longitude);
    }

    private String getLocationType() {
      return geometry.locationType;
    }

    /**
     * Object relational mapper for Gson. It represents the "geometry" json object within one
     * element of json array "results" of the Google Geocoding response.
     */
    @SuppressWarnings("unused")
    private static class Geometry {
      @SerializedName("location") private Location location;
      @SerializedName("location_type") private String locationType;
    }

    /**
     * Object relational mapper for Gson. It represents the "location" json object within the
     * json object "geometry" of the Google Geocoding response.
     */
    @SuppressWarnings("unused")
    private static class Location {
      @SerializedName("lat") private Double latitude;
      @SerializedName("lng") private Double longitude;
    }
  }
}
