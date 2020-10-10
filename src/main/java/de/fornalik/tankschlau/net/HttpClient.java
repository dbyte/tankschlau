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

import java.io.IOException;
import java.util.Optional;

/**
 * Interface for HTTP client used by this application.
 */
public interface HttpClient {

  /**
   * @return The request which was last used invoking {@link #newCall(Request, StringResponse)}.
   */
  Optional<Request> getRequest();

  /**
   * Calls the web service synchronously and extracts its response body by invoking
   * {@link Response#setBody}, while the type T of the body is a concrete {@link String} here.
   *
   * @param request  A configured {@link Request} object.
   * @param response A default-initialized {@link BaseResponse} object, which will be populated by
   *                 the server's response data, then being passed back as the return value.
   * @return The populated {@link Response} object which was passed to this method.
   * @throws IOException If something went wrong while handling communication etc.
   */
  Response<String> newCall(final Request request, StringResponse response) throws IOException;
}
