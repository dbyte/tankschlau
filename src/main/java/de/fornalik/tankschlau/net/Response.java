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
 * The HTTP response interface used by this application. T determines the type of the response body.
 *
 * @param <T> The type of the response body.
 */
public interface Response<T> {

  /**
   * @return Optional error message for errors which have not been thrown at request time.
   * <span style="color:red;">Important: </span>Empty Optional if no errors were detected.
   */
  Optional<String> getErrorMessage();

  /**
   * @param message Sets an error message for errors which have not been thrown at request time.
   *                Do not set at all (or null) if no error were encountered.
   */
  void setErrorMessage(String message);

  /**
   * Gets the final response body data as T, while T is determined by the implementation
   * of {@link BaseResponse}.
   *
   * @return Optional body of the response as T, e.g. <code>String</code>. Or an empty Optional
   * if there were errors which have not been thrown at request time.
   */
  Optional<T> getBody();

  /**
   * Sets the final body data.
   *
   * @param data Some data of type T, while T is defined by concrete implementation
   *             of the {@link BaseResponse}.
   */
  void setBody(T data);

  /**
   * @return Some information about the client/server transaction, directly provided
   * by the server or enriched by ourselves. Should return empty string if no info is available.
   */
  String getStatus();

  /**
   * @param data Some information about the client/server transaction, directly provided
   *             by the server or enriched by ourselves.
   */
  void setStatus(String data);
}
