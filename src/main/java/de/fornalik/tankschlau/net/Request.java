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

import java.net.URL;
import java.util.Map;

/**
 * The HTTP request interface used by this application.
 */
public interface Request {

  /**
   * Get HTTP request method previously set by {@link #setHttpMethod(HttpMethod)}
   *
   * @return ex. "GET" or "POST"
   */
  HttpMethod getHttpMethod();

  /**
   * @param method "GET" or "POST"
   */
  void setHttpMethod(HttpMethod method);

  /**
   * @return ex. https://creativecommons.tankerkoenig.de/json/list.php?
   * @see #setBaseUrl(URL)
   */
  URL getBaseUrl();

  /**
   * Sets the most generic URL for the call.
   *
   * @param url ex. https://creativecommons.tankerkoenig.de/json/list.php?
   */
  void setBaseUrl(URL url);

  /**
   * Adds a URL parameter if HTTP method is GET.
   * Should result in UTF-8 encoded query string which is then added to the requests existing URL
   * parameters.
   *
   * @param key   ex. "sort"
   * @param value ex. "price"
   */
  void putUrlParameter(String key, String value);

  /**
   * @param urlParameter An existing URL parameter which gets extended by the given
   *                     string. The string won't be encoded.
   * @param s            A string to append (unencoded) to the given URL parameter.
   * @param encoding     ex. "UTF-8" {@link java.net.URLEncoder}, or null if the string must not
   *                     be encoded.
   */
  void appendUrlString(String urlParameter, String s, String encoding);

  /**
   * Getter for URL parameters.
   *
   * @return key/value pairs of all URL parameters which were previously
   * added by {@link #putUrlParameter(String, String)}.
   */
  Map<String, String> getUrlParameters();

  /**
   * Adds a parameter to the request body, needed if HTTP method is POST.
   * Values should NOT be encoded at this point, because we should have full control at the
   * time the parameters are converted to something suitable for the HTTP client impl.
   *
   * @param key   ex. "name"
   * @param value ex. "Joe" - do NOT encode to UTF-8 at this point.
   */
  void putBodyParameter(String key, String value);

  /**
   * Getter for body parameters.
   *
   * @return key/value pairs of all body parameters which were previously
   * added by {@link #putBodyParameter(String, String)}.
   */
  Map<String, String> getBodyParameters();

  /**
   * Adds an entry to the request header.
   *
   * @param key   ex. "Accept"
   * @param value ex. "application/json; charset=utf-8"
   */
  void putHeader(String key, String value);

  /**
   * Getter for HTTP headers.
   *
   * @return key/value pairs of all HTTP headers which were previously
   * added by {@link #putHeader(String, String)}.
   */
  Map<String, String> getHeaders();

  /**
   * Literals used to define the method of an HTTP request.
   */
  enum HttpMethod {
    GET, POST
  }
}
