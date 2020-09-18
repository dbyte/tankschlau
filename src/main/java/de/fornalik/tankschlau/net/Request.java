package de.fornalik.tankschlau.net;

import java.net.URL;
import java.util.Map;

/**
 * The HTTP request interface used by this application.
 */
public interface Request {

  /**
   * Get HTTP request method previously set by {@link #setHttpMethod(String)}
   *
   * @return ex. "GET" or "POST"
   */
  String getHttpMethod();

  /**
   * @param method "GET" or "POST"
   */
  void setHttpMethod(String method);

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
   * Results in UTF-8 encoded query string which is then added to the requests existing URL
   * parameters.
   *
   * @param key   ex. "sort"
   * @param value ex. "price"
   */
  void addUrlParameter(String key, String value);

  /**
   * Getter for URL parameters.
   *
   * @return key/value pairs of all URL parameters which were previously
   * added by {@link #addUrlParameter(String, String)}.
   */
  Map<String, String> getUrlParameters();

  /**
   * Adds an entry to the request header.
   *
   * @param key   ex. "Accept"
   * @param value ex. "application/json; charset=utf-8"
   */
  void addHeader(String key, String value);

  /**
   * Getter for HTTP headers.
   *
   * @return key/value pairs of all HTTP headers which were previously
   * added by {@link #addHeader(String, String)}.
   */
  Map<String, String> getHeaders();
}
