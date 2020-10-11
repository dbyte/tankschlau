package de.fornalik.tankschlau.net;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class BaseResponseTest {
  // Also test with a non string variant.
  private BaseResponse<Byte> byteResponse;
  // Test with a string implementation variant.
  private BaseResponse<String> stringResponse;

  @BeforeEach
  void setUp() {
    this.stringResponse = new BaseResponse<String>() {};
    this.byteResponse = new BaseResponse<Byte>() {};
  }

  @Test
  void create_returnsProperlyInitializedInstance() {
    // when then
    assertEquals(Optional.empty(), stringResponse.getBody());
    assertEquals(Optional.empty(), stringResponse.getErrorMessage());
    assertEquals("", stringResponse.getStatus());

    assertEquals(Optional.empty(), byteResponse.getBody());
    assertEquals(Optional.empty(), byteResponse.getErrorMessage());
    assertEquals("", stringResponse.getStatus());
  }

  @Test
  void setBody_properlyProcessesStringArgument() {
    // given
    String givenString = "This is a string which represents body data";

    // when then
    assertDoesNotThrow(() -> stringResponse.setBody(givenString));
    assertTrue(stringResponse.getBody().isPresent());
    assertEquals(givenString, stringResponse.getBody().get());
  }

  @Test
  void setBody_properlyProcessesByteArgument() {
    // given
    Byte expectedByte = 0b1001010;

    // when then
    assertDoesNotThrow(() -> byteResponse.setBody(expectedByte));
    assertTrue(byteResponse.getBody().isPresent());
    assertEquals(expectedByte, byteResponse.getBody().get());
  }

  @Test
  void setStatus_setsGivenString() {
    // given
    String givenStatus = "Some status text: Response OK.";

    // when then
    assertDoesNotThrow(() -> stringResponse.setStatus(givenStatus));
    assertEquals(givenStatus, stringResponse.getStatus());
  }

  @Test
  void getStatus_returnsEmptyStringIfValueIsNull() {
    // when then
    assertDoesNotThrow(() -> stringResponse.setStatus(null));
    assertEquals("", stringResponse.getStatus());
  }

  @Test
  void setErrorMessage_setsAndTrimsGivenMessage() {
    // given
    String givenString = "    This is an error string  ";
    String expectedString = "This is an error string";

    // when then
    assertDoesNotThrow(() -> stringResponse.setErrorMessage(givenString));
    assertTrue(stringResponse.getErrorMessage().isPresent());
    assertEquals(expectedString, stringResponse.getErrorMessage().get());
  }
}