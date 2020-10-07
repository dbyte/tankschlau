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

import com.google.gson.JsonObject;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Abstract HTTP request class with base functionalities.
 */
public abstract class BaseRequest implements Request {

  private final Map<String, String> urlParameters = new HashMap<>();
  private final Map<String, String> bodyParameters = new HashMap<>();
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
    String encodedValue = encodeString(value, "UTF-8");
    this.urlParameters.put(key, encodedValue);
  }

  @Override
  public void appendUrlString(String urlParameter, String s, String encoding) {
    String existingValue = getUrlParameters().get(urlParameter);

    if (existingValue == null)
      throw new NoSuchElementException(
          String.format("URL parameter %s does not exist.", urlParameter));

    if (encoding != null)
      s = encodeString(s, encoding);

    String newValue = existingValue + s;

    // Replace old value.
    this.urlParameters.put(urlParameter, newValue);
  }

  @Override
  public Map<String, String> getUrlParameters() {
    return this.urlParameters;
  }

  @Override
  // TODO unit test
  public void addBodyParameter(String key, String value) {
    // Do NOT encode at this point.
    this.bodyParameters.put(key, value);
  }

  @Override
  // TODO unit test
  public Map<String, String> getBodyParameters() {
    return bodyParameters;
  }

  @SuppressWarnings("unchecked")
  @Override
  // TODO unit test
  public String convertBodyParameters(String to) {
    if ("JSON_STRING".equals(to)) {
      JsonObject jsonObject = new JsonObject();
      getBodyParameters().forEach(jsonObject::addProperty);
      return jsonObject.toString();

    } else {
      throw new IllegalArgumentException("\"" + to + "\" not implemented for conversion.");
    }
  }

  @Override
  public void addHeader(String key, String value) {
    this.headers.put(key, value);
  }

  @Override
  public Map<String, String> getHeaders() {
    return this.headers;
  }

  private String encodeString(String s, String enc) {
    String encodedString;

    try {
      encodedString = URLEncoder.encode(s, enc);
    }
    catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e.getMessage());
    }

    return encodedString;
  }
}
