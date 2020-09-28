package de.fornalik.tankschlau.net;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * We only test getters/setters with non-standard functionality here.
 */
class StringResponseTest {

  @Test
  void create_returnsProperlyInitializedInstance() {
    // when
    Response response = StringResponse.create();

    // then
    assertEquals(Optional.empty(), response.getBody());
    assertEquals(Optional.empty(), response.getErrorMessage());
  }

  @Test
  void setBody_properlyProcessesStringArgument() {
    // given
    Response response = StringResponse.create();
    String givenString = "This is a string which represents body data";

    // when then
    assertDoesNotThrow(() -> response.setBody(givenString));
    assertTrue(response.getBody().isPresent());
    assertEquals(givenString, response.getBody().get());
  }

  @Test
  void setBody_throwsOnInvalidArgumentType() {
    // given
    Response response = StringResponse.create();
    byte[] bytes = new byte[]{12, 127, 67, 13};

    // when then
    assertThrows(ClassCastException.class, () -> response.setBody(bytes));
  }
}