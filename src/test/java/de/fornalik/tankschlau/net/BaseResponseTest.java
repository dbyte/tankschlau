package de.fornalik.tankschlau.net;

import de.fornalik.tankschlau.service.TransactInfo;
import de.fornalik.tankschlau.service.TransactInfoImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

class BaseResponseTest {
  private ResponseBody responseBodyMock;
  private TransactInfo transactInfoMock;

  private BaseResponse baseResponse; // SUT

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
    assertNotNull(baseResponse.getBody());
    assertNotNull(baseResponse.getTransactInfo());
  }

  @Test
  void getBody_returnsInstanceOfResponseBody() {
    assertNotNull(baseResponse);

    // when then
    assertEquals(responseBodyMock, baseResponse.getBody());
  }

  @Test
  void getTransactInfo_returnsInstanceOfTransactInfo() {
    assertNotNull(transactInfoMock);

    // when then
    assertEquals(transactInfoMock, baseResponse.getTransactInfo());
  }
}