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
import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.JsonResponse;
import de.fornalik.tankschlau.net.StringResponse;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

// TODO unit tests, move some from GoogleGeocodingClientTest to here.

/**
 * Concrete implementation of {@link StringResponse} for Google Geocoding webservice.
 * Locks the type of the response body to <code>String</code>.
 *
 * @see <a href="https://maps.googleapis.com/maps/api/geocode/json">API base URL</a>,
 * <a href="https://developers.google.com/maps/documentation/geocoding/overview#GeocodingResponses">Google documentation: GeocodingResponses</a>
 */
class GoogleGeocodingResponse extends JsonResponse<Geo> {
  private final Gson jsonProvider;

  GoogleGeocodingResponse(Gson jsonProvider) {
    this.jsonProvider = Objects.requireNonNull(jsonProvider);
  }

  @Override
  public Optional<Geo> fromJson(String jsonString) {

    ResponseDTO responseDto = jsonProvider.fromJson(
        jsonString,
        ResponseDTO.class);

    if (responseDto == null) {
      setErrorMessage("JSON string could not be converted. String is:\n" + jsonString);
      setStatus("ERROR");
      return Optional.empty();
    }

    Optional<Geo> geo = Optional.empty();

    if (responseDto.status != null && !"".equals(responseDto.status)) {
      setStatus(responseDto.status);
    }

    if (responseDto.message != null && !"".equals(responseDto.message)) {
      setErrorMessage(responseDto.message);
    }

    if (responseDto.results.size() > 0) {
      /* results is always initialized at construction time of ResponseDTO, so can't be null.
      From here, we can trust that webservice has set values for latitude, longitude
      and location type. */
      ResultDTO firstResult = responseDto.results.get(0);
      geo = Optional.of(firstResult.getAsGeo());
    }

    return geo;
  }

  /**
   * Object relational mapper for Gson. It must correlate with the root level json object
   * of the Google Geocoding response.
   */
  private static class ResponseDTO {
    @SerializedName("status") String status;
    @SerializedName("error_message") String message;

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    @SerializedName("results")
    final ArrayList<ResultDTO> results;

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
    @SerializedName("geometry") private ResultDTO.Geometry geometry;

    Geo getAsGeo() {
      return new Geo(geometry.location.latitude, geometry.location.longitude);
    }

    String getLocationType() {
      return geometry.locationType;
    }

    /**
     * Object relational mapper for Gson. It represents the "geometry" json object within one
     * element of json array "results" of the Google Geocoding response.
     */
    @SuppressWarnings("unused")
    private static class Geometry {
      @SerializedName("location") private ResultDTO.Location location;
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
