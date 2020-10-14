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

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Supporting class for classes inherited from {@link BaseRequest} that additionally must be able
 * to form a JSON request body.
 */
public class JsonRequestImpl extends BaseRequest implements JsonRequest {

  @Override
  public String computeJsonBody() {
    Map<String, String> bodyParams = getBodyParameters();

    if (bodyParams.size() == 0)
      return "{}";

    String jsonString = "";

    jsonString += bodyParams.keySet().stream()
        .filter(Objects::nonNull)
        .map(key -> {
          String property = "\"" + key + "\"";
          String value = bodyParams.get(key);
          return (value == null ? property + ": null" : property + ": \"" + value + "\"");
        })
        .collect(Collectors.joining(", ", "{", "}"));

    return jsonString;
  }
}