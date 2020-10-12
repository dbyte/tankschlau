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
import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.net.JsonResponse;
import de.fornalik.tankschlau.net.StringResponse;
import de.fornalik.tankschlau.util.StringLegalizer;
import de.fornalik.tankschlau.webserviceapi.common.Licensed;

import java.util.Objects;
import java.util.Optional;

// TODO unit tests

/**
 * Concrete implementation of {@link StringResponse} for tankerkoenig.de webservice.
 * Locks the type of the response body to <code>String</code>.
 */
public class TankerkoenigResponse extends JsonResponse<TankerkoenigResponse.ResponseDto>
    implements Licensed {
  private final Gson jsonProvider;
  ResponseDto responseDto;

  TankerkoenigResponse(Gson jsonProvider) {
    this.jsonProvider = Objects.requireNonNull(jsonProvider);
    this.responseDto = null;
  }

  @Override
  public Optional<ResponseDto> fromJson(String jsonString) {
    // Deserialize
    ResponseDto responseDto = jsonProvider.fromJson(jsonString, ResponseDto.class);

    if (responseDto == null) {
      setErrorMessage("JSON string could not be converted. String is:\n" + jsonString);
      setStatus("ERROR");
      return Optional.empty();
    }

    if (responseDto.status != null && !"".equals(responseDto.status))
      setStatus(responseDto.status);

    if (responseDto.message != null && !"".equals(responseDto.message))
      setErrorMessage(responseDto.message);

    /*
    From here, we can trust that tankerkoenig.de service has set all expected values.
    */
    return Optional.of(responseDto);
  }

  @Override
  public String getLicenseString() {
    if (responseDto == null)
      return "";

    return responseDto.getLicense();
  }

  /**
   * Class provides object relational mapping support for Gson. It must correlate with the
   * root level JSON object of the webservice response.
   */
  @SuppressWarnings("unused")
  static class ResponseDto {
    @SerializedName("ok") private boolean ok;
    @SerializedName("license") private String license;
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;

    public boolean isOk() {
      return ok;
    }

    public void setOk(boolean ok) {
      this.ok = ok;
    }

    public String getLicense() {
      return nullToEmpty(license);
    }

    public String getStatus() {
      return nullToEmpty(status);
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public String getMessage() {
      return nullToEmpty(message);
    }

    public void setMessage(String message) {
      this.message = StringLegalizer.create(message).safeTrim().end();
    }

    private String nullToEmpty(String s) {
      return StringLegalizer.create(s).nullToEmpty().end();
    }
  }

  /*
   * Class provides object relational mapping support for {@link Gson}. Convert a tankerkoenig.de
   * JSON response file to a {@link PetrolStation}.
   *//*
  @SuppressWarnings("unused")
  private static class PetrolStationDto {
    @SerializedName("id") private UUID uuid;
    @SerializedName("name") private String name;
    @SerializedName("brand") private String brand;
    @SerializedName("isOpen") private boolean isOpen;
    @SerializedName("street") private String street;
    @SerializedName("houseNumber") private String houseNumber;
    @SerializedName("place") private String city;
    @SerializedName("postCode") private String postCode;
    @SerializedName("lat") private Double lat;
    @SerializedName("lng") private Double lng;
    @SerializedName("dist") private Double distanceKm;
    @SerializedName("diesel") private Double diesel;
    @SerializedName("e5") private Double e5;
    @SerializedName("e10") private Double e10;
  }*/
}
