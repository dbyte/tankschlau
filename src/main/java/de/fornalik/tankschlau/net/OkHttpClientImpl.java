package de.fornalik.tankschlau.net;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

/**
 * okhttp3 implementation of the {@link HttpClient} interface.
 */
public class OkHttpClientImpl implements HttpClient {
  private Request request;

  public OkHttpClientImpl() {
  }

  @Override
  public Optional<Request> getRequest() {
    return Optional.ofNullable(this.request);
  }

  @Override
  public Response newCall(final Request request, Response response) throws IOException {
    this.request = request;

    okhttp3.HttpUrl url = createUrl();
    okhttp3.Request okhttpRequest = createRequest(url);
    okhttp3.Response okhttpResponse = callServer(okhttpRequest);

    if (okhttpResponse.body() != null)
      response.setBodyString(okhttpResponse.body().string());
    else
      response.setErrorMessage(getErrorMessage(okhttpResponse));

    return response;
  }

  private okhttp3.HttpUrl createUrl() {
    HttpUrl.Builder urlBuilder = Objects
        .requireNonNull(HttpUrl.parse(request.getBaseUrl().toString()))
        .newBuilder();

    request.getUrlParameters().forEach(urlBuilder::addQueryParameter);
    return urlBuilder.build();
  }

  private okhttp3.Request createRequest(okhttp3.HttpUrl url) {
    okhttp3.Request.Builder okhttpRequestBuilder = new okhttp3.Request.Builder()
        .url(url.toString())
        .method(request.getMethod(), null);

    request.getHeaders().forEach(okhttpRequestBuilder::addHeader);
    return okhttpRequestBuilder.build();
  }

  private okhttp3.Response callServer(okhttp3.Request okhttpRequest) throws IOException {
    OkHttpClient okHttpClient = new OkHttpClient();
    Call call = okHttpClient.newCall(okhttpRequest);
    return call.execute();
  }

  private String getErrorMessage(okhttp3.Response okhttpResponse) {
    return "Body of http response object is null. "
        + "HTTP status message: "
        + okhttpResponse.message()
        + "\nResponse headers: \n"
        + okhttpResponse.headers().toString();
  }
}