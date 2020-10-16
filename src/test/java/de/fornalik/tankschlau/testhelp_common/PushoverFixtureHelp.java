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

package de.fornalik.tankschlau.testhelp_common;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.storage.TransactInfo;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PushoverFixtureHelp {
  public ResponseDTO objectFixture;
  public String jsonFixture;

  /**
   * Computes two test-fixture objects by reading a JSON response fixture file.<br/>
   * 1) {@link GeocodingFixtureHelp.ResponseDTO} which we can use e.g. for equality checks.<br/>
   * 2) {@link JsonObject} of the JSON file fixture.
   *
   * @param resName Resource path as String. Note that the implicit resource root path must not
   *                be included here.
   */
  public void setupFixture(String resName) {
    Objects.requireNonNull(resName);

    FileReader reader1 = ClassLoaderHelp.getFileReaderForResource(resName, getClass());
    FileReader reader2 = ClassLoaderHelp.getFileReaderForResource(resName, getClass());

    Gson gson = new Gson();

    objectFixture = gson.fromJson(reader1, PushoverFixtureHelp.ResponseDTO.class);
    jsonFixture = JsonParser.parseReader(reader2).toString();
  }

  public void assertEqualValues(TransactInfo transactInfo) {
    assert transactInfo != null;
    assert objectFixture != null;

    // Begin test

    assertEquals(objectFixture.status, Integer.parseInt(transactInfo.getStatus()));

    /*
    Lenient message-check, because at some conditions, we concatenate custom message strings with
    the ones delivered by the webservice.
    */
    String actualErrorMsg = transactInfo.getErrorMessage().orElse("");

    assertTrue(
        (objectFixture.errors.size() == 0 && "".equals(actualErrorMsg))
            || objectFixture.errors.stream().anyMatch(actualErrorMsg::contains),

        "\nExpected error message to contain\n"
            + "\"" + objectFixture.errors + "\"\n"
            + "but actually is\n"
            + "\"" + transactInfo.getErrorMessage() + "\"");
  }

  /**
   * Transfer class to easily convert a JSON response file to a
   * test-fixture response. Conversion is currently processed by the {@link Gson}
   * library. <br>
   * All DTO fields are public mutable for testing purposes. Also, all primitives are wrapped
   * to be able to null them for testing purposes.
   */
  public static class ResponseDTO {
    @SerializedName("status") Integer status;
    @SerializedName("request") String requestToken;
    @SerializedName("secret") String secretError;
    @SerializedName("token") String tokenError;
    @SerializedName("user") String userError;
    @SerializedName("errors") ArrayList<String> errors;

    ResponseDTO() {
      errors = new ArrayList<>();
    }
  }
}
