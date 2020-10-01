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
 * The HTTP response interface used by this application.
 */
public interface Response {

  /**
   * @return Optional error message for errors which have not been thrown at request time.
   * Empty Optional if no errors were detected.
   */
  Optional<String> getErrorMessage();

  /**
   * @param message Sets an error message for errors which have not been thrown at request time.
   *                Do not set at all (or null) if no error were encountered.
   */
  void setErrorMessage(String message);

  /**
   * Gets the final response body data as a string.
   *
   * @return Optional body of the response as string, e.g. a JSON string. Or an empty Optional
   * if there were errors which have not been thrown at request time - in this case,
   * {@link #getErrorMessage()} should return the message which prevented the body from
   * being converted to string.
   */
  <T> Optional<T> getBody();

  /**
   * Sets the final string data.
   *
   * @param <T>  The type of data.
   * @param data Some data of type T.
   */
  <T> void setBody(T data);
}
