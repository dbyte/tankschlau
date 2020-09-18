package de.fornalik.tankschlau.net;

import java.net.URL;
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
    this.urlParameters.put(key, value);
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
