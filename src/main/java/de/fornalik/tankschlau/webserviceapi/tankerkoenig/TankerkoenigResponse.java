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
import de.fornalik.tankschlau.net.BaseResponse;
import de.fornalik.tankschlau.net.JsonResponse;
import de.fornalik.tankschlau.net.ResponseBody;
import de.fornalik.tankschlau.storage.TransactInfo;

import java.lang.reflect.Type;
import java.util.Objects;
import java.util.Optional;

/**
 * Concrete implementation of {@link JsonResponse} for tankerkoenig.de webservice.
 * Locks the type of the response body to <code>String</code>.
 */
public class TankerkoenigResponse extends BaseResponse implements JsonResponse {

  private final Gson jsonProvider;
  private final ResponseBody
  private ResponseDto responseDto;
  private String licenseString;

  public TankerkoenigResponse(
      Gson jsonProvider,
      ResponseBody responseBody,
      TransactInfo transactInfo) {

    super(responseBody, transactInfo);
    this.jsonProvider = Objects.requireNonNull(jsonProvider);
    this.responseDto = null;
    this.licenseString = "";
  }

  @Override
  public <T> Optional<T> fromJson(String jsonString, Class<T> targetClass) {
    // Deserialize root level data of of the webservice's JSON response and push it
    // into our existing TransactInfo object.

    responseDto = jsonProvider.fromJson(jsonString, (Type) targetClass);

    if (responseDto == null) {
      getTransactInfo().setErrorMessage(
          "JSON string could not be converted. String is:\n"
          + jsonString);

      getTransactInfo().setStatus("ERROR");
      return Optional.empty();
    }

    if (!responseDto.getStatus().isEmpty())
      getTransactInfo().setStatus(responseDto.getStatus());

    if (!responseDto.getMessage().isEmpty())
      getTransactInfo().setErrorMessage(responseDto.getMessage());

    if (!responseDto.getLicense().isEmpty())
      licenseString = responseDto.getLicense();

    return Optional.empty();
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
      return s != null ? s : "";
    }
  }
}
