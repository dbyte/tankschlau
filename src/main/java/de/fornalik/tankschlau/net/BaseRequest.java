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

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Abstract HTTP request class with base functionalities.
 */
public abstract class BaseRequest implements Request {

  private final Map<String, String> urlParameters = new HashMap<>();
  private final Map<String, String> headers = new HashMap<>();
  private URL baseUrl;
  private HttpMethod httpMethod;

  protected BaseRequest() {
  }

  @Override
  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  @Override
  public void setHttpMethod(HttpMethod in) {
    this.httpMethod = in;
  }

  @Override
  public URL getBaseUrl() {
    return this.baseUrl;
  }

  @Override
  public void setBaseUrl(URL url) {
    this.baseUrl = url;
  }

  @Override
  public void addUrlParameter(String key, String value) {
    String encodedValue;

    try {
      encodedValue = URLEncoder.encode(value, "UTF-8");

    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e.getMessage());
    }

    this.urlParameters.put(key, encodedValue);
  }

  @Override
  public Map<String, String> getUrlParameters() {
    return this.urlParameters;
  }

  @Override
  public void addHeader(String key, String value) {
    this.headers.put(key, value);
  }

  @Override
  public Map<String, String> getHeaders() {
    return headers;
  }
}
