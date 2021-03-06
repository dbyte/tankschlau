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

package de.fornalik.tankschlau.station;

import org.apache.commons.lang3.StringUtils;

public enum PetrolType {

  DIESEL("diesel"),
  E5("e5"),
  E10("e10");

  private final String jsonKey;

  PetrolType(String jsonKey) {
    this.jsonKey = jsonKey;
  }

  public String getJsonKey() {
    return this.jsonKey;
  }

  public String getReadableName() {
    return StringUtils.capitalize(this.name().toLowerCase());
  }
}
