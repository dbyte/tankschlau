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
import de.fornalik.tankschlau.net.BaseResponse;
import de.fornalik.tankschlau.net.JsonResponse;
import de.fornalik.tankschlau.net.ResponseBody;
import de.fornalik.tankschlau.service.TransactInfo;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Implementation of {@link JsonResponse} for Google Geocoding webservice.
 *
 * @see <a href="https://maps.googleapis.com/maps/api/geocode/json">API base URL</a>,
 * <a href="https://developers.google.com/maps/documentation/geocoding/overview#GeocodingResponses">Google documentation: GeocodingResponses</a>
 */
public class GoogleGeocodingResponse extends BaseResponse implements JsonResponse {
  private static final Logger LOGGER = Logger.getLogger(GoogleGeocodingResponse.class.getName());
  private final Gson jsonProvider;

  public GoogleGeocodingResponse(
      Gson jsonProvider,
      ResponseBody responseBody,
      TransactInfo transactInfo) {
    super(Objects.requireNonNull(responseBody), Objects.requireNonNull(transactInfo));
    this.jsonProvider = Objects.requireNonNull(jsonProvider);
  }

  @Override
  public <T> Optional<T> fromJson(String jsonString, Class<T> targetClass) {
    /*
    1. Deserialize data from JSON data which are of informal type -
    like status, licence string, error message because of wrong API key etc.
    */
    ResponseDTO responseDto = jsonProvider.fromJson(jsonString, ResponseDTO.class);

    getTransactInfo().setLicence("Geo data powered by Google.");

    if (responseDto == null) {
      String errMsg = "JSON string could not be converted. String is: " + jsonString;
      getTransactInfo().setErrorMessage(errMsg);
      getTransactInfo().setStatus("ERROR");
      LOGGER.warning(errMsg);

      return Optional.empty();
    }

    if (responseDto.status != null && !"".equals(responseDto.status))
      getTransactInfo().setStatus(responseDto.status);

    if (responseDto.message != null && !"".equals(responseDto.message))
      getTransactInfo().setErrorMessage(responseDto.message);


    if (responseDto.results.size() == 0) {
      getTransactInfo().setErrorMessage(responseDto.message);
      return Optional.empty();
    }

    /*
    2. From here, we can trust that Google service has set values for latitude, longitude and
    location type. List "results" is always initialized at construction time of ResponseDTO,
    so can't be null.
    */
    ResultDTO firstResult = responseDto.results.get(0);
    return Optional.of(targetClass.cast(firstResult.getAsGeo()));
  }

  /**
   * Class provides object relational mapping support for Gson. It must correlate with the
   * root level json object of the Google Geocoding response.
   */
  static class ResponseDTO {
    @SerializedName("results")
    ArrayList<ResultDTO> results;
    @SerializedName("status") String status;
    @SerializedName("error_message") String message;

    ResponseDTO() {
      this.results = new ArrayList<>();
    }
  }

  /**
   * Class provides object relational mapping support for Gson. It represents the json array
   * "results" of the Google Geocoding response.
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
     * Class provides object relational mapping support for Gson. It represents the "geometry"
     * json object within one element of json array "results" of the Google Geocoding response.
     */
    @SuppressWarnings("unused")
    private static class Geometry {
      @SerializedName("location") private ResultDTO.Location location;
      @SerializedName("location_type") private String locationType;
    }

    /**
     * Class provides object relational mapping support for Gson. It represents the "location"
     * json object within the json object "geometry" of the Google Geocoding response.
     */
    @SuppressWarnings("unused")
    private static class Location {
      @SerializedName("lat") private Double latitude;
      @SerializedName("lng") private Double longitude;
    }
  }
}
