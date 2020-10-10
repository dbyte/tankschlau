package de.fornalik.tankschlau.net;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;

/**
 * We only test getters/setters with non-standard functionality here.
 */
class StringResponseTest {
  private static StringResponse stringResponse;

  @BeforeAll
  static void beforeAll() {
    stringResponse = mock(StringResponse.class, CALLS_REAL_METHODS);
  }

  @AfterAll
  static void afterAll() {
    stringResponse = null;
  }

  @Test
  void create_returnsProperlyInitializedInstance() {
    // when then
    assertEquals(Optional.empty(), stringResponse.getBody());
    assertEquals(Optional.empty(), stringResponse.getErrorMessage());
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
  void setErrorMessage_setsGivenMessage() {
    // given
    String givenString = "This is an error string";

    // when then
    assertDoesNotThrow(() -> stringResponse.setErrorMessage(givenString));
    assertTrue(stringResponse.getErrorMessage().isPresent());
    assertEquals(givenString, stringResponse.getErrorMessage().get());
  }
}