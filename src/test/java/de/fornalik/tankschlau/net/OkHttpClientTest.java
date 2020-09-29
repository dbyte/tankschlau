package de.fornalik.tankschlau.net;

import okhttp3.MediaType;
import okhttp3.Protocol;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class OkHttpClientTest {
  private Request requestMock;
  private StringResponse stringResponseMock;

  private okhttp3.OkHttpClient okHttp3ClientMock;
  private okhttp3.Request okHttp3Request;
  private okhttp3.Call okHttp3CallMock;

  @BeforeEach
  void beforeAll() {
    // Mock this app's Request & Response

    requestMock = Mockito.mock(Request.class);
    stringResponseMock = Mockito.mock(StringResponse.class, Mockito.CALLS_REAL_METHODS);

    // Mock used fragments of okhttp3 library

    // okhttp3.Request is a final class, so not mockable.
    okHttp3Request = new okhttp3.Request.Builder()
        .url("https://does-not-matter.com")
        .build();

    okHttp3ClientMock = Mockito.mock(okhttp3.OkHttpClient.class);
    okHttp3CallMock = Mockito.mock(okhttp3.Call.class);

    Mockito.when(requestMock.getBaseUrl()).thenReturn(okHttp3Request.url().url());
    Mockito.when(requestMock.getHttpMethod()).thenReturn(Request.HttpMethod.GET);
    Mockito.when(okHttp3ClientMock.newCall(Mockito.any())).thenReturn(okHttp3CallMock);
  }

  @AfterEach
  void tearDown() {
    requestMock = null;
    stringResponseMock = null;
    okHttp3ClientMock = null;
    okHttp3Request = null;
    okHttp3CallMock = null;
  }

  @Test
  void construct_initializesProperly() {
    // when
    HttpClient okHttpClient = new OkHttpClient();

    // then
    assertEquals(Optional.empty(), okHttpClient.getRequest());
  }

  @Test
  void newCall_returnsProperResponseBody() throws IOException {
    // given
    String expectedContent = "Some string value which should be content of the response body.";
    HttpClient sut = new OkHttpClient(okHttp3ClientMock);
    this.helpSetupOkHttp3StringResponseMock(okHttp3Request, expectedContent);

    // when
    Response actualResponse = sut.newCall(requestMock, stringResponseMock);

    // then
    assertTrue(actualResponse.getBody().isPresent());
    assertEquals(expectedContent, actualResponse.getBody().get());
  }

  @Test
  void newCall_throwsIfBodyIsNull() throws IOException {
    // given
    okhttp3.ResponseBody nullBody = null;
    HttpClient sut = new OkHttpClient(okHttp3ClientMock);

    //noinspection ConstantConditions
    this.helpSetupOkHttp3ResponseMock(okHttp3Request, nullBody);

    // when then
    assertThrows(
        IOException.class,
        () -> sut.newCall(requestMock, stringResponseMock));
  }

  @Test
  void newCall_throwsIfBodyDataIsNotAStringResponse() throws IOException {
    // given
    Response nonStringResponse = new NonStringResponseStub();
    HttpClient sut = new OkHttpClient(okHttp3ClientMock);
    this.helpSetupOkHttp3StringResponseMock(okHttp3Request, "");

    // when then
    assertThrows(
        UnsupportedOperationException.class,
        () -> sut.newCall(requestMock, nonStringResponse));
  }

  private void helpSetupOkHttp3StringResponseMock(okhttp3.Request request, String content)
  throws IOException {
    okhttp3.ResponseBody body = okhttp3.ResponseBody
        .create(MediaType.parse(
            "application/json; charset=utf-8"), content);

    helpSetupOkHttp3ResponseMock(request, body);
  }

  private void helpSetupOkHttp3ResponseMock(okhttp3.Request request, okhttp3.ResponseBody body)
  throws IOException {
    Objects.requireNonNull(
        okHttp3CallMock,
        "okHttp3CallMock is not set yet! Set before calling this method.");

    okhttp3.Response preparedResponse = new okhttp3.Response.Builder()
        .body(body)
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(0)
        .message("")
        .build();

    Mockito.when(okHttp3CallMock.execute()).thenReturn(preparedResponse);
  }

  private static class NonStringResponseStub implements Response {
    @Override
    public Optional<String> getErrorMessage() {
      return Optional.empty();
    }

    @Override
    public void setErrorMessage(String message) {
    }

    @Override
    public <T> Optional<T> getBody() {
      return Optional.empty();
    }

    @Override
    public <T> void setBody(T data) {
    }
  }
}