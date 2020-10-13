package de.fornalik.tankschlau.net;

import de.fornalik.tankschlau.storage.TransactInfo;
import de.fornalik.tankschlau.storage.TransactInfoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

// TODO some tests must be taken by ResponseBodyTest ! Move them there.

class BaseResponseTest {
  private ResponseBody responseBodyMock;
  private TransactInfo transactInfoMock;

  private BaseResponse baseResponse; // SUT®

  @BeforeEach
  void setUp() {
    this.responseBodyMock = mock(ResponseBodyImpl.class, CALLS_REAL_METHODS);
    this.transactInfoMock = mock(TransactInfoImpl.class, CALLS_REAL_METHODS);

    this.baseResponse = new BaseResponse(responseBodyMock, transactInfoMock);
  }

  @Test
  void create_returnsProperlyInitializedInstance() {
    // when
    this.baseResponse = new BaseResponse(responseBodyMock, transactInfoMock);

    // then
    assertEquals(responseBodyMock, baseResponse.getBody());
    assertEquals(transactInfoMock, baseResponse.getTransactInfo());

    assertNull(baseResponse.getBody().getData(String.class));
    assertEquals(Optional.empty(), baseResponse.getTransactInfo().getErrorMessage());
    assertEquals("", baseResponse.getTransactInfo().getStatus());
  }

  @Test
  void setBody_properlyProcessesStringArgument() {
    // given
    String givenString = "This is a string which represents body data";

    // when then
    assertNotNull(baseResponse.getBody());
    assertDoesNotThrow(() -> baseResponse.getBody().setData(givenString));
    assertEquals(givenString, baseResponse.getBody().getData(String.class));
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