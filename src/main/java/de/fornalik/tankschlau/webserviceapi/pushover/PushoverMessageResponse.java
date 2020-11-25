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
import de.fornalik.tankschlau.service.TransactInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

/**
 * Implementation of {@link JsonResponse} for pushover.net webservice.
 *
 * @see
 * <a href="https://pushover.net/api#response">API response documentation: https://pushover.net/api#response</a>
 */
public class PushoverMessageResponse extends BaseResponse implements JsonResponse {
  private static final Logger LOGGER = Logger.getLogger(PushoverMessageResponse.class.getName());
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
      // Create error message.
      // Append our custom error to possibly existing ones (from server response).

      String existingStatus = getTransactInfo().getStatus();
      getTransactInfo().setStatus(String.join(" & ", existingStatus, "DESERIALIZATION_ERROR"));

      String errorMsg = "JSON string could not be deserialized. String is: " + jsonString;
      Optional<String> existingErrorMsg = getTransactInfo().getErrorMessage();
      if (existingErrorMsg.isPresent()) {
        errorMsg = existingErrorMsg.get() + " & " + errorMsg;
      }
      getTransactInfo().setErrorMessage(errorMsg);
      LOGGER.warning(errorMsg);
    }
    else {
      getTransactInfo().setStatus(String.valueOf(responseDto.status));

      String pushoverErrorMsg = processErrorList(responseDto.errors);
      if (!"".equals(pushoverErrorMsg)) {
        getTransactInfo().setErrorMessage(pushoverErrorMsg);
      }
    }

    return Optional.empty();
  }

  private String processErrorList(List<String> errorList) {
    // Must return null if no errors exist.
    if (errorList == null || errorList.isEmpty())
      return null;

    String customErrorMsg = "Pushover reported " + errorList.size() + " error(s): ";
    return customErrorMsg + String.join(", ", errorList);
  }

  /**
   * Class provides object relational mapping support for Gson. It must correlate with the
   * root level json object of the pushover.net response.
   */
  private static class ResponseDTO {
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
