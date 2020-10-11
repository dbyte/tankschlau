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
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.Response;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class GeocodingFixtureHelp {
  public ResponseDTO objectFixture;
  public String jsonFixture;

  /**
   * Computes two test-fixture objects by reading a JSON response fixture file.<br/>
   * 1) {@link ResponseDTO} which we can use e.g. for equality checks.<br/>
   * 2) {@link JsonObject} of the JSON file fixture.
   *
   * @param resName Resource path as String. Note that the implicit resource root path must not
   *                be included here.
   */
  public void setupFixture(String resName) {
    Objects.requireNonNull(resName);

    FileReader reader1 = FixtureFiles.getFileReaderForResource(resName);
    FileReader reader2 = FixtureFiles.getFileReaderForResource(resName);

    Gson gson = new Gson();

    objectFixture = gson.fromJson(reader1, ResponseDTO.class);
    jsonFixture = JsonParser.parseReader(reader2).toString();
  }

  /**
   * Deep check for value equality of a fixture with a {@link Geo}.
   *
   * @param geo The {@link Geo} to be checked for deep value equality.
   */
  public void assertEqualValues(Geo geo) {
    /* Preconditions for running the test. Note these checks are not subject to the test itself.
    Thus, we don't use Junit assertions here. */
    assert objectFixture != null;
    assert objectFixture.results != null && objectFixture.results.size() == 1;

    ResultDTO fixture = objectFixture.results.get(0);

    // Begin test

    assertEquals(fixture.getAsGeo().getLatitude(), geo.getLatitude());
    assertEquals(fixture.getAsGeo().getLongitude(), geo.getLongitude());
    assertEquals(fixture.getAsGeo().getDistance(), Optional.empty());
  }

  public void assertEqualValues(Response<String> response) {
    assert response != null;
    assert objectFixture != null;

    // Begin test

    assertEquals(objectFixture.status, response.getStatus());
    assertEquals(objectFixture.message, response.getErrorMessage().orElse(""));

    /* According to Google Geocoding conventions, we assert that the results array is never null,
     even if we got a response with an error message. */
    assertNotNull(objectFixture.results);
  }

  /**
   * Transfer class to easily convert a GeocodingClient JSON response file to a
   * test-fixture response. Conversion is currently processed by the {@link Gson}
   * library. <br>
   * All DTO fields are public mutable for testing purposes. Also, all primitives are wrapped
   * to be able to null them for testing purposes.
   *
   * @see de.fornalik.tankschlau.webserviceapi.common.GeocodingClient
   */
  public static class ResponseDTO {
    @SerializedName("status") public String status;
    @SerializedName("error_message") public String message;
    @SerializedName("results") public ArrayList<ResultDTO> results;

    ResponseDTO() {
      results = new ArrayList<>();

      // As "message" is missing in JSON if no error occurred, it is by convention set
      // to empty in our production class. So we do same here.
      message = "";
    }
  }

  /**
   * Transfer class to easily convert a GoogleGeocodingClient JSON response file to a
   * test-fixture. Conversion is currently processed by {@link Gson}.
   * <br>
   * All DTO fields are public mutable for testing purposes. Also, all primitives are wrapped
   * to be able to null them for testing purposes.
   */
  public static class ResultDTO {
    @SerializedName("geometry") Geometry geometry;

    public Geo getAsGeo() {
      return new Geo(geometry.location.latitude, geometry.location.longitude);
    }

    public String getLocationType() {
      return geometry.locationType;
    }

    private static class Geometry {
      @SerializedName("location") Location location;
      @SerializedName("location_type") String locationType;
    }

    private static class Location {
      @SerializedName("lat") Double latitude;
      @SerializedName("lng") Double longitude;
    }
  }
}
