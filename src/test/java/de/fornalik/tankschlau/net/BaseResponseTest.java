package de.fornalik.tankschlau.net;

import de.fornalik.tankschlau.storage.TransactInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// TODO some tests must be taken by ResponseBodyTest ! Move them there.

class BaseResponseTest {
  private static ResponseBody responseBodyMock = mock(ResponseBody.class);
  private static TransactInfo transactInfoMock = mock(TransactInfo.class);

  private BaseResponse baseResponse; // SUT

  @BeforeEach
  void setUp() {
    when(responseBodyMock).thenCallRealMethod();
    when(transactInfoMock).thenCallRealMethod();
    this.baseResponse = new BaseResponse(responseBodyMock, transactInfoMock);
  }

  @Test
  void create_returnsProperlyInitializedInstance() {
    // when then
    assertEquals(Optional.empty(), baseResponse.getBody());
    assertEquals(Optional.empty(), baseResponse.getTransactInfo().getErrorMessage());
    assertEquals("", baseResponse.getTransactInfo().getStatus());
  }

  @Test
  void setBody_properlyProcessesStringArgument() {
    // given
    String givenString = "This is a string which represents body data";

    // when then
    assertTrue(baseResponse.getBody().isPresent());
    assertDoesNotThrow(() -> baseResponse.getBody().get().setData(givenString));
    assertEquals(givenString, baseResponse.getBody().get().getData(String.class));
  }

  @Test
  void setStatus_setsGivenString() {
    // given
    String givenStatus = "Some status text: Response OK.";

    // when then
    assertDoesNotThrow(() -> baseResponse.getTransactInfo().setStatus(givenStatus));
    assertEquals(givenStatus, baseResponse.getTransactInfo().getStatus());
  }

  @Test
  void getStatus_returnsEmptyStringIfValueIsNull() {
    // when then
    assertDoesNotThrow(() -> baseResponse.getTransactInfo().setStatus(null));
    assertEquals("", baseResponse.getTransactInfo().getStatus());
  }

  @Test
  void setErrorMessage_setsAndTrimsGivenMessage() {
    // given
    String givenString = "    This is an error string  ";
    String expectedString = "This is an error string";

    // when then
    assertDoesNotThrow(() -> baseResponse.getTransactInfo().setErrorMessage(givenString));
    assertTrue(baseResponse.getTransactInfo().getErrorMessage().isPresent());
    assertEquals(expectedString, baseResponse.getTransactInfo().getErrorMessage().get());
  }
}