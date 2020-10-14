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

package de.fornalik.tankschlau.webserviceapi.pushover;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.net.BaseResponse;
import de.fornalik.tankschlau.net.JsonResponse;
import de.fornalik.tankschlau.net.ResponseBody;
import de.fornalik.tankschlau.storage.TransactInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

// TODO unit tests

/**
 * Implementation of {@link JsonResponse} for pushover.net webservice.
 *
 * @see
 * <a href="https://pushover.net/api#response">API response documentation: https://pushover.net/api#response</a>
 */
public class PushoverMessageResponse extends BaseResponse implements JsonResponse {
  private final Gson jsonProvider;

  public PushoverMessageResponse(
      Gson jsonProvider,
      ResponseBody responseBody,
      TransactInfo transactInfo) {
    super(Objects.requireNonNull(responseBody), Objects.requireNonNull(transactInfo));
    this.jsonProvider = Objects.requireNonNull(jsonProvider);
  }

  @Override
  public <T> Optional<T> fromJson(String jsonString, Class<T> targetClass) {
    /*
    Deserialize data from JSON which are of informal type -
    like status, licence string, error message because of invalid API key etc.
    */
    ResponseDTO responseDto = jsonProvider.fromJson(jsonString, ResponseDTO.class);

    getTransactInfo().setLicence("Push messages provided by pushover.net");

    if (responseDto == null) {
      // Concat possibly existing values (from server response) with our custom additions.
      String existingStatus = getTransactInfo().getStatus();
      Optional<String> existingErrorMsg = getTransactInfo().getErrorMessage();

      getTransactInfo().setStatus(String.join(" + ", existingStatus, "DESERIALIZATION_ERROR"));

      getTransactInfo().setErrorMessage(
          String.join(" ", existingErrorMsg.orElse(""),
              "JSON string could not be deserialized. String is:",
              jsonString));

      return Optional.empty();
    }

    getTransactInfo().setStatus(String.valueOf(responseDto.status));

    getTransactInfo().setErrorMessage(processErrorList(responseDto.errors));

    return Optional.empty();
  }

  private String processErrorList(List<String> errorList) {
    // Must return null if no errors exist.
    if (errorList == null || errorList.size() == 0)
      return null;

    String stringBuilder = "Pushover reported " + errorList.size() + " error(s): ";
    return String.join(", ", stringBuilder);
  }

  /**
   * Class provides object relational mapping support for Gson. It must correlate with the
   * root level json object of the pushover.net response.
   */
  static class ResponseDTO {
    @SerializedName("status") int status;
    @SerializedName("request") String requestToken;
    @SerializedName("secret") String secretError;
    @SerializedName("token") String tokenError;
    @SerializedName("user") String userError;
    @SerializedName("errors") ArrayList<String> errors;

    ResponseDTO() {
      this.errors = new ArrayList<>();
    }
  }
}
