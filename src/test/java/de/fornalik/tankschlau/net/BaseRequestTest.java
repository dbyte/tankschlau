package de.fornalik.tankschlau.net;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests for abstract base class, testing non-abstract methods.
 * We do only test getters/setters with non-standard functionality here.
 */
class BaseRequestTest {
  BaseRequest baseRequest;

  @BeforeEach
  void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException,
      InstantiationException {
    //Set up a mock which inherits from the abstract SUT.
    BaseRequest preparedMock = Mockito.mock(BaseRequest.class, Mockito.CALLS_REAL_METHODS);
    baseRequest = preparedMock.getClass().getDeclaredConstructor().newInstance();
  }

  @AfterEach
  void tearDown() {
    baseRequest = null;
  }

  @Test
  void urlMapIsEmptyAfterConstructionOfTheInheritedClass() {
    assertEquals(0, baseRequest.getUrlParameters().size());
  }

  @Test
  void putUrlParameter_putsMapElementsProperly() {
    // given
    String givenValue01 = "someValue";
    String givenValue02 = "someOtherValue";

    // when
    baseRequest.putUrlParameter("testKey01", givenValue01);
    baseRequest.putUrlParameter("testKey02", givenValue02);

    // then
    assertEquals(givenValue01, baseRequest.getUrlParameters().get("testKey01"));
    assertEquals(givenValue02, baseRequest.getUrlParameters().get("testKey02"));
  }

  @Test
  void putUrlParameter_encodesValuesUrlEncodedProperly() {
    // given
    String givenValue01 = "Hallo, hier sind Umlaute öäü";
    String givenValue02 = "Und hier Sonderzeichen &?";

    // when
    baseRequest.putUrlParameter("testKey01", givenValue01);
    baseRequest.putUrlParameter("testKey02", givenValue02);

    // then
    assertEquals(
        "Hallo%2C+hier+sind+Umlaute+%C3%B6%C3%A4%C3%BC",
        baseRequest.getUrlParameters().get("testKey01"));

    assertEquals(
        "Und+hier+Sonderzeichen+%26%3F",
        baseRequest.getUrlParameters().get("testKey02"));
  }

  @Test
  void appendUrlParameterString_appendsNonUrlEncodedStringToValue() {
    // given
    baseRequest.putUrlParameter("key1", "value1_part1");

    // when
    baseRequest.appendUrlParameterString("key1", "_part2_ÖÄÜß", null);
    String actualUrlString = baseRequest.getUrlParameters().get("key1");

    // then
    assertEquals("value1_part1_part2_ÖÄÜß", actualUrlString);
  }

  @Test
  void appendUrlParameterString_appendsUtf8UrlEncodedStringToValue() {
    // given
    baseRequest.putUrlParameter("key1", "value1_part1");

    // when
    baseRequest.appendUrlParameterString("key1", "_part2_ÖÄÜß", "UTF-8");
    String actualUrlString = baseRequest.getUrlParameters().get("key1");

    // then
    assertEquals("value1_part1_part2_%C3%96%C3%84%C3%9C%C3%9F", actualUrlString);
  }

  @Test
  void appendUrlParameterString_DoesNotDoubleEncodeExistingEncodedPart() {
    // given
    // (Value being implicitly converted to UTF-8)
    baseRequest.putUrlParameter("key1", "+part1+");

    // when
    baseRequest.appendUrlParameterString("key1", "+++,+++", null);
    baseRequest.appendUrlParameterString("key1", "_part2_ _", "UTF-8");

    String actualUrlString = baseRequest.getUrlParameters().get("key1");

    // then
    assertEquals("%2Bpart1%2B+++,+++_part2_+_", actualUrlString);
    System.out.println(actualUrlString);
  }

  @Test
  void appendUrlParameterString_throwsRuntimeExceptionOnUnsupportedUrlEncoding() {
    // given
    baseRequest.putUrlParameter("key1", "value1_part1");

    // when
    assertThrows(
        BaseRequest.RequestEncodingException.class,
        () -> baseRequest
            .appendUrlParameterString("key1", "_part2", "This-Encoding-Is-NotSupported"));
  }

  @Test
  void putBodyParameter_putsProperMapEntries() {
    // given
    String givenValue01 = "someValue";
    String givenValue02 = "someOtherValue";

    // when
    baseRequest.putBodyParameter("testKey01", givenValue01);
    baseRequest.putBodyParameter("testKey02", givenValue02);

    // then
    assertEquals(givenValue01, baseRequest.getBodyParameters().get("testKey01"));
    assertEquals(givenValue02, baseRequest.getBodyParameters().get("testKey02"));
  }

  @Test
  void headerMapIsEmptyAfterConstructionOfTheInheritedClass() {
    assertEquals(0, baseRequest.getHeaders().size());
  }

  @Test
  void putHeader_putsMapElementsProperly() {
    // given
    String givenValue01 = "application/json; charset=utf-8";
    String givenValue02 = "no-cache";

    // when
    baseRequest.putHeader("Accept", givenValue01);
    baseRequest.putHeader("Cache-Control", givenValue02);

    // then
    assertEquals(givenValue01, baseRequest.getHeaders().get("Accept"));
    assertEquals(givenValue02, baseRequest.getHeaders().get("Cache-Control"));
  }
}