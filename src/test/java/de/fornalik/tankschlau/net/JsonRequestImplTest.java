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

package de.fornalik.tankschlau.net;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonRequestImplTest {
  private JsonRequestImpl jsonRequest;
  private String actualJsonString, expectedJsonString;

  @BeforeEach
  void setUp() {
    this.jsonRequest = new JsonRequestImpl();
    this.actualJsonString = null;
    this.expectedJsonString = null;
  }

  @Test
  void computeJsonBody_computesExpectedJsonString() {
    // given
    jsonRequest.putBodyParameter("myKey", "myString value");
    jsonRequest.putBodyParameter("someOtherKey", "Some other great value");
    expectedJsonString = "{\"someOtherKey\": \"Some other great value\", \"myKey\": \"myString "
        + "value\"}";

    // when
    actualJsonString = jsonRequest.computeJsonBody();

    // then
    assertEquals(expectedJsonString, actualJsonString);
  }

  @Test
  void computeJsonBody_skipsMapEntriesWhichKeyIsNull() {
    // given
    jsonRequest.putBodyParameter(null, "This entry should be excluded");
    jsonRequest.putBodyParameter("myKey", "My value");
    jsonRequest.putBodyParameter("someOtherKey", "Some other great value");
    expectedJsonString = "{\"someOtherKey\": \"Some other great value\", \"myKey\": \"My value\"}";

    // when
    actualJsonString = jsonRequest.computeJsonBody();

    // then
    assertEquals(expectedJsonString, actualJsonString);
  }

  @Test
  void computeJsonBody_keepsMapEntriesWhichKeyIsNonNullAndValueIsNull() {
    // given
    jsonRequest.putBodyParameter("myKey", "My value");
    jsonRequest.putBodyParameter("entryWhichShouldBeIncluded", null);
    jsonRequest.putBodyParameter("someOtherKey", "Some other great value");
    expectedJsonString = "{\"someOtherKey\": \"Some other great value\", "
        + "\"entryWhichShouldBeIncluded\": null, \"myKey\": \"My value\"}";

    // when
    actualJsonString = jsonRequest.computeJsonBody();

    // then
    assertEquals(expectedJsonString, actualJsonString);
  }

  @Test
  void computeJsonBody_returnsEmptyJsonObjectStringIfNoBodyParamsExist() {
    // given
    jsonRequest = new JsonRequestImpl();
    expectedJsonString = "{}";

    // when
    actualJsonString = jsonRequest.computeJsonBody();

    // then
    assertEquals(expectedJsonString, actualJsonString);
  }
}