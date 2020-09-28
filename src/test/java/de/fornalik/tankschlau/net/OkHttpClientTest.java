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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OkHttpClientTest {
  // Mock this app's Request & Response
  private Request requestMock;
  private StringResponse stringResponseMock;

  // Mock okhttp3 library
  private okhttp3.OkHttpClient okHttp3ClientMock;
  private okhttp3.Request okHttp3Request;
  private okhttp3.Call okHttp3CallMock;

  @BeforeEach
  void beforeAll() {
    requestMock = Mockito.mock(Request.class);
    stringResponseMock = Mockito.mock(StringResponse.class, Mockito.CALLS_REAL_METHODS);

    // okhttp3.Request is a final class, so not mockable.
    okHttp3Request = new okhttp3.Request.Builder()
        .url("https://does-not-matter.com")
        .build();

    // Mock out internal calls of okhttp3
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
    this.helpSetupOkHttp3ResponseMock(okHttp3Request, expectedContent);

    // when
    Response actualResponse = sut.newCall(requestMock, stringResponseMock);

    // then
    assertTrue(actualResponse.getBody().isPresent());
    assertEquals(expectedContent, actualResponse.getBody().get());
  }

  private void helpSetupOkHttp3ResponseMock(okhttp3.Request request, String content)
  throws IOException {
    Objects.requireNonNull(
        okHttp3CallMock,
        "okHttp3CallMock is not set yet! Set before calling this method.");

    okhttp3.Response preparedResponse = new okhttp3.Response.Builder()
        .body(okhttp3.ResponseBody
                  .create(MediaType.parse(
                      "application/json; charset=utf-8"), content))
        .request(request)
        .protocol(Protocol.HTTP_1_1)
        .code(0)
        .message("")
        .build();

    Mockito.when(okHttp3CallMock.execute()).thenReturn(preparedResponse);
  }

}