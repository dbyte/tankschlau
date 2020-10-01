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
   * @return The request which was last used invoking {@link #newCall(Request, Response)}
   */
  Optional<Request> getRequest();

  /**
   * Calls the web service synchronously and extracts its response body by invoking
   * {@link Response#setBody(Object)}, while the type of the body depends on the passed instance
   * of an implementation of {@link Response}).
   *
   * @param request  A configured {@link Request} object.
   * @param response A default-initialized {@link Response} object, which will be populated by the
   *                 server's response data, then being passed back as the return value.
   * @return The populated {@link Response} object which was passed to this method.
   * @throws IOException If something went wrong while handling communication etc.
   */
  Response newCall(final Request request, Response response) throws IOException;

  /**
   * Calls the web service and defaults to convert its response body to a
   * string (by using a new instance of {@link StringResponse}).
   * If a different response body data type is needed, we must instead call overloaded
   * {@link #newCall(Request, Response)}, passing an instance of a specialized implementation
   * of {@link Response}.
   *
   * @param request A configured {@link Request} object.
   * @return see {@link #newCall(Request, Response)}
   * @throws IOException see {@link #newCall(Request, Response)}
   * @see #newCall(Request, Response)
   */
  Response newCall(final Request request) throws IOException;
}
