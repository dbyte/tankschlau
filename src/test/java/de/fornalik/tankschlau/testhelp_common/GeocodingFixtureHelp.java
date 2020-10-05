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

import java.io.FileReader;
import java.util.Objects;

public class GeocodingFixtureHelp {
  public JsonObject jsonFixture;

  /**
   * Computes two test-fixture objects by reading a JSON response fixture file.<br/>
   * 1) {@link DomainFixtureHelp.ResponseDTO} which we can use e.g. for equality checks.<br/>
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

    // objectFixture = gson.fromJson(reader1, DomainFixtureHelp.ResponseDTO.class);
    jsonFixture = (JsonObject) JsonParser.parseReader(reader2);
  }
}
