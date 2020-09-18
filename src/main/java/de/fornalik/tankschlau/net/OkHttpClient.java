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

  public OkHttpClient() {
    createOkHttpClientSingleton();
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
  public Response newCall(final Request request, Response response)
      throws IOException {
    this.request = request;

    okhttp3.HttpUrl url = createUrl();
    okhttp3.Request okhttpRequest = createRequest(url);
    okhttp3.Response okhttpResponse = callServer(okhttpRequest); //throws

    if (okhttpResponse.body() != null) {
      if (response instanceof StringResponse)
        response.setBody(okhttpResponse.body().string());
      else
        throw new UnsupportedOperationException(
            "response.setBody not implemented for incoming type of Response.");

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

    request.getUrlParameters().forEach(urlBuilder::addQueryParameter);
    return urlBuilder.build();
  }

  private okhttp3.Request createRequest(okhttp3.HttpUrl url) {
    okhttp3.Request.Builder okhttpRequestBuilder = new okhttp3.Request.Builder()
        .url(url.toString())
        .method(request.getHttpMethod(), null);

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
