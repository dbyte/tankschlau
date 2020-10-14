package de.fornalik.tankschlau.net;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for abstract base class, testing non-abstract methods.
 * We do only test getters/setters with non-standard functionality here.
 */
class BaseRequestTest {
  BaseRequest concreteRequestMock;

  @BeforeEach
  void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
      InstantiationException {
    //Set up a mock which inherits from the abstract SUT.
    BaseRequest preparedMock = Mockito.mock(BaseRequest.class, Mockito.CALLS_REAL_METHODS);
    concreteRequestMock = preparedMock.getClass().getDeclaredConstructor().newInstance();
  }

  @AfterEach
  void tearDown() {
    concreteRequestMock = null;
  }

  @Test
  void urlMapIsEmptyAfterConstructionOfTheInheritedClass() {
    assertEquals(0, concreteRequestMock.getUrlParameters().size());
  }

  @Test
  void putUrlParameter_addsMapElementsProperly() {
    // given
    String givenValue01 = "someValue";
    String givenValue02 = "someOtherValue";

    // when
    concreteRequestMock.putUrlParameter("testKey01", givenValue01);
    concreteRequestMock.putUrlParameter("testKey02", givenValue02);

    // then
    assertEquals(givenValue01, concreteRequestMock.getUrlParameters().get("testKey01"));
    assertEquals(givenValue02, concreteRequestMock.getUrlParameters().get("testKey02"));
  }

  @Test
  void putUrlParameter_encodesValuesUrlEncodedProperly() {
    // given
    String givenValue01 = "Hallo, hier sind Umlaute öäü";
    String givenValue02 = "Und hier Sonderzeichen &?";

    // when
    concreteRequestMock.putUrlParameter("testKey01", givenValue01);
    concreteRequestMock.putUrlParameter("testKey02", givenValue02);

    // then
    assertEquals(
        "Hallo%2C+hier+sind+Umlaute+%C3%B6%C3%A4%C3%BC",
        concreteRequestMock.getUrlParameters().get("testKey01"));

    assertEquals(
        "Und+hier+Sonderzeichen+%26%3F",
        concreteRequestMock.getUrlParameters().get("testKey02"));
  }

  @Test
  void headerMapIsEmptyAfterConstructionOfTheInheritedClass() {
    assertEquals(0, concreteRequestMock.getHeaders().size());
  }

  @Test
  void putHeader_addsMapElementsProperly() {
    // given
    String givenValue01 = "application/json; charset=utf-8";
    String givenValue02 = "no-cache";

    // when
    concreteRequestMock.putHeader("Accept", givenValue01);
    concreteRequestMock.putHeader("Cache-Control", givenValue02);

    // then
    assertEquals(givenValue01, concreteRequestMock.getHeaders().get("Accept"));
    assertEquals(givenValue02, concreteRequestMock.getHeaders().get("Cache-Control"));
  }
}