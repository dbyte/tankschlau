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

  private static okhttp3.OkHttpClient okHttpClientSingleton;
  private Request request;

  /**
   * This constructor should be the only one being used in production.
   */
  public OkHttpClient() {
    this.createOkHttpClientSingleton();
  }

  /**
   * This constructor should only be used for testing purposes as it does not rely on
   * a one time instantiation of the underlying {@link okhttp3.OkHttpClient}.
   */
  public OkHttpClient(okhttp3.OkHttpClient okHttp3Client) {
    OkHttpClient.okHttpClientSingleton = okHttp3Client;
  }

  @Override
  public Optional<Request> getRequest() {
    return Optional.ofNullable(this.request);
  }

  @Override
  public Response newCall(final Request request) throws IOException {
    return this.newCall(request, StringResponse.create());
  }

  @Override
  public Response newCall(final Request request, Response response) throws IOException {
    this.request = request;

    okhttp3.HttpUrl url = createUrl();
    okhttp3.Request okhttpRequest = createRequest(url);
    okhttp3.Response okhttpResponse = callServer(okhttpRequest); //throws

    if (okhttpResponse.body() != null) {
      if (response instanceof StringResponse)
        response.setBody(okhttpResponse.body().string());
      else
        throw new UnsupportedOperationException(
            "response.setBody not implemented for incoming type of Response: "
                + response.getClass().getTypeName());

    } else {
      String message = "Body of response is null. " + getDetails(okhttpResponse);
      throw new IOException(message);
    }

    return response;
  }

  private okhttp3.HttpUrl createUrl() {

    okhttp3.HttpUrl.Builder urlBuilder = Objects
        .requireNonNull(okhttp3.HttpUrl.parse(request.getBaseUrl().toString()))
        .newBuilder();

    request.getUrlParameters().forEach(urlBuilder::addEncodedQueryParameter);
    return urlBuilder.build();
  }

  private okhttp3.Request createRequest(okhttp3.HttpUrl url) {
    okhttp3.Request.Builder okhttpRequestBuilder = new okhttp3.Request.Builder()
        .url(url.toString())
        .method(request.getHttpMethod().name(), null);

    request.getHeaders().forEach(okhttpRequestBuilder::addHeader);
    return okhttpRequestBuilder.build();
  }

  private okhttp3.Response callServer(okhttp3.Request okhttpRequest) throws IOException {
    okhttp3.Call call = okHttpClientSingleton.newCall(okhttpRequest);
    return call.execute(); // throws
  }

  private String getDetails(okhttp3.Response okhttpResponse) {
    return "HTTP status message: "
        + okhttpResponse.message()
        + "\nResponse headers:\n"
        + okhttpResponse.headers().toString();
  }

  private void createOkHttpClientSingleton() {
    // Singleton for OkHttpClient as recommended by okhttp crew.
    if (OkHttpClient.okHttpClientSingleton == null)
      OkHttpClient.okHttpClientSingleton = new okhttp3.OkHttpClient();
  }
}
