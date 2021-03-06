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
 * Interface for HTTP client used by this application.
 */
public interface HttpClient {

  /**
   * @return The request which was last used invoking {@link #newCall(Request, Response, Class)}.
   */
  Optional<Request> getRequest();

  /**
   * Calls the web service synchronously and extracts its response body.
   *
   * @param request            A configured {@link Request} object.
   * @param response           A default-initialized implementation of {@link Response}, which
   *                           will be populated by the server's response data, then being passed
   *                           back as the return value.
   * @param typeOfResponseData Type variable for the body data, e.g. <code>String.class</code>
   * @param <T>                The class of a type.
   * @return The populated {@link Response} object which was passed to this method.
   */
  <T> Response newCall(
      final Request request,
      final Response response,
      final Class<T> typeOfResponseData);
}
