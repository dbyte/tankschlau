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

package de.fornalik.tankschlau.webserviceapi.tankerkoenig.testhelp.response;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class JsonFixtureTestsuite {
  protected Pair<JsonResponseHelp, JsonObject> responseHelp;
  protected JsonResponseHelp objectFixture;
  protected JsonObject jsonFixture;

  @BeforeEach
  protected void setUpSuite() {
    responseHelp = null;
    objectFixture = null;
    jsonFixture = null;
  }

  @AfterEach
  protected void tearDownSuite() {
    responseHelp = null;
    objectFixture = null;
    jsonFixture = null;
  }

  protected void setupFixture(String resourceName) {
    responseHelp = JsonResponseHelp.createFromJsonFile(resourceName);
    objectFixture = responseHelp.getLeft();
    jsonFixture = responseHelp.getRight();
  }

  protected void setupSingleFixture(String resourceName) {
    responseHelp = JsonResponseHelp.createFirstStationFromJsonFile(resourceName);
    objectFixture = responseHelp.getLeft();
    jsonFixture = responseHelp.getRight();
  }
}
