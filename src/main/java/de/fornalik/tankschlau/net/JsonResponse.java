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

import java.util.Optional;

/**
 * Additional interface for body data which contains a JSON string.
 */
public interface JsonResponse extends Response {

  /**
   * @param jsonString  Some JSON string to deserialize.
   * @param targetClass Type of class T which is the deserialization target.
   * @param <T>         e.g. <code>String.class</code>.
   */
  <T> Optional<T> fromJson(String jsonString, Class<T> targetClass);
}
