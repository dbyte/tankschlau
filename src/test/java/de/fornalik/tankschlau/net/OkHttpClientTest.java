package de.fornalik.tankschlau.net;

import okhttp3.MediaType;
import okhttp3.Protocol;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class OkHttpClientTest {
  HttpClient okHttpClient;
  private Request requestMock;
  private Response<String> stringResponseMock;
  private Response<String> actualResponse;
  private okhttp3.Request okHttp3Request;
  private okhttp3.Call okHttp3CallMock;

  @BeforeEach
  void setUp() {
    // Mock this app's Request & Response

    requestMock = mock(Request.class);
    stringResponseMock = mock(StringResponse.class, Mockito.CALLS_REAL_METHODS);

    // Mock used fragments of okhttp3 library

    // okhttp3.Request is a final class, so not mockable.
    okHttp3Request = new okhttp3.Request.Builder()
        .url("https://does-not-matter.com")
        .build();

    okhttp3.OkHttpClient okHttp3ClientMock = mock(okhttp3.OkHttpClient.class);
    okHttp3CallMock = mock(okhttp3.Call.class);

    Mockito.when(requestMock.getBaseUrl()).thenReturn(okHttp3Request.url().url());
    Mockito.when(requestMock.getHttpMethod()).thenReturn(Request.HttpMethod.GET);
    Mockito.when(okHttp3ClientMock.newCall(Mockito.any())).thenReturn(okHttp3CallMock);

    actualResponse = null;
    okHttpClient = new OkHttpClient(okHttp3ClientMock);
  }

  @Test
  void construct_initializesProperly() {
    assertEquals(Optional.empty(), okHttpClient.getRequest());
  }

  @Test
  void newCall_returnsProperResponseBody() throws IOException {
    // given
    String expectedContent = "Some string value which should be content of the response body.";
    helpSetupOkHttp3BaseResponseMock(okHttp3Request, expectedContent);

    // when
    actualResponse = okHttpClient.newCall(requestMock, (StringResponse) stringResponseMock);

    // then
    assertTrue(actualResponse.getBody().isPresent());
    assertEquals(expectedContent, actualResponse.getBody().get());
  }

  @Test
  void newCall_doesNotThrowIfBodyIsNull() throws IOException {
    // given
    helpSetupOkHttp3ResponseMock(okHttp3Request, null);

    // when then
    assertDoesNotThrow(() -> okHttpClient.newCall(
        requestMock,
        (StringResponse) stringResponseMock));
  }

  @Test
  void newCall_setsProperErrorMessageIfBodyIsNull() throws IOException {
    // given
    helpSetupOkHttp3ResponseMock(okHttp3Request, null);
    String expectedPartOfErrorMessage = "Body of response is null.";

    // when
    actualResponse = okHttpClient.newCall(requestMock, (StringResponse) stringResponseMock);

    // then
    assertFalse(actualResponse.getBody().isPresent());
    assertTrue(actualResponse.getErrorMessage().isPresent());

    Optional<String> actualErrorMessage = actualResponse.getErrorMessage();

    assertTrue(
        actualErrorMessage.get().contains(expectedPartOfErrorMessage),
        "\nExpected message: \"" + expectedPartOfErrorMessage + "\"\n"
            + "Actual message: \"" + actualErrorMessage + "\"");
  }

  private void helpSetupOkHttp3BaseResponseMock(okhttp3.Request request, String content)
  throws IOException {

    okhttp3.ResponseBody body = okhttp3.ResponseBody
        .create(
            MediaType.parse("application/json; charset=utf-8"),
            content);

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
}