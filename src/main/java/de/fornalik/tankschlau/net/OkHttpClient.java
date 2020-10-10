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
import java.util.Objects;
import java.util.Optional;

/**
 * okhttp3 implementation of the {@link HttpClient} interface.
 */
public class OkHttpClient implements HttpClient {

  private final okhttp3.OkHttpClient okHttp3Client;
  private Request request;

  /**
   * Constructor
   * <br><br>
   * In production, do a one-time-instantiation of this class at startup time.
   * Side note: According to the okhttp3 docs, it's perfectly o.k. to its the client for the
   * lifecycle of the app.
   *
   * @param okHttp3Client Instance of {@link okhttp3.OkHttpClient} to be adapted.
   */
  public OkHttpClient(okhttp3.OkHttpClient okHttp3Client) {
    this.okHttp3Client = Objects.requireNonNull(okHttp3Client);
  }

  @Override
  public Optional<Request> getRequest() {
    return Optional.ofNullable(this.request);
  }

  @Override
  public StringResponse newCall(final Request request, StringResponse response) {
    okhttp3.Response okhttpResponse;

    try {
      okhttpResponse = this.realCall(request, response);
    }

    catch (IOException | IllegalStateException e) {
      return response;
    }

    try {
      response.setBody(Objects.requireNonNull(okhttpResponse.body()).string());
    }

    catch (IOException | NullPointerException e) {
      String msg = "Body of response could not be converted to string.";
      response.setErrorMessage(msg + " " + getDetails(okhttpResponse));
    }

    return response;
  }

  private okhttp3.Response realCall(final Request request, Response<?> response)
  throws IOException, IllegalStateException {
    this.request = request;

    okhttp3.HttpUrl url = createUrl();
    okhttp3.Request okhttpRequest = createRequest(url);
    okhttp3.Response okhttpResponse;

    try {
      okhttpResponse = callServer(okhttpRequest); //throws
    }
    catch (IOException e) {
      response.setErrorMessage(e.getMessage());
      throw e;
    }

    if (okhttpResponse.body() == null) {
      response.setErrorMessage("Body of response is null.\n" + getDetails(okhttpResponse));
      //noinspection OptionalGetWithoutIsPresent
      throw new IllegalStateException(response.getErrorMessage().get());
    }

    return okhttpResponse;
  }

  private okhttp3.HttpUrl createUrl() {

    okhttp3.HttpUrl.Builder urlBuilder = Objects
        .requireNonNull(okhttp3.HttpUrl.parse(request.getBaseUrl().toString()))
        .newBuilder();

    request.getUrlParameters().forEach(urlBuilder::addEncodedQueryParameter);
    return urlBuilder.build();
  }

  // TODO unit test for use of createJsonRequestBody()
  private okhttp3.Request createRequest(okhttp3.HttpUrl url) {
    okhttp3.Request.Builder okhttpRequestBuilder = new okhttp3.Request.Builder()
        .url(url.toString())
        .method(request.getHttpMethod().name(), createJsonRequestBody());

    request.getHeaders().forEach(okhttpRequestBuilder::addHeader);

    return okhttpRequestBuilder.build();
  }

  // TODO unit test
  private okhttp3.RequestBody createJsonRequestBody() {
    if (request.getBodyParameters().isEmpty())
      // Note: Body is only allowed to be null for HTTP "GET" request type.
      return null;

    String jsonBody = request.convertBodyParameters("JSON_STRING");

    return okhttp3.RequestBody.create(
        okhttp3.MediaType.parse("application/json; charset=utf-8"),
        jsonBody
    );
  }

  private okhttp3.Response callServer(okhttp3.Request okhttpRequest) throws IOException {
    okhttp3.Call realCall = okHttp3Client.newCall(okhttpRequest);
    return realCall.execute(); // throws
  }

  private String getDetails(okhttp3.Response okhttpResponse) {
    return "HTTP status message: "
        + okhttpResponse.message()
        + "\nResponse headers:\n"
        + okhttpResponse.headers().toString();
  }
}
