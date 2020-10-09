package de.fornalik.tankschlau.util;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MyToStringBuilderTest {
  private MyToStringBuilder myToStringBuilder;
  private FixtureClass givenInstance;
  private String expectedStartString, expectedEndString, actualString;

  @BeforeEach
  void setUp() {
    givenInstance = new FixtureClass();
    myToStringBuilder = new MyToStringBuilder(givenInstance);
    expectedStartString = null;
    expectedEndString = null;
    actualString = null;
  }

  @Test
  void toString_producesImplicitShortClassNameStyle() {
    // given
    givenInstance.someString = "is not tested here";
    myToStringBuilder.append("is not tested here", givenInstance.someString);

    expectedStartString = getClass().getSimpleName() + "." + FixtureClass.class.getSimpleName();

    // when
    actualString = myToStringBuilder.toString();

    // then
    assertEquals(expectedStartString, StringUtils.substringBefore(actualString, "@"));
  }

  @Test
  void toString_producesExpectedStringForFields() {
    // given
    givenInstance.someString = "This is another test value ÖÄÜ.";
    givenInstance.someInt = 2347;

    myToStringBuilder.append("someString", givenInstance.someString);
    myToStringBuilder.append("someInt", givenInstance.someInt);

    expectedEndString = "someString=This is another test value ÖÄÜ.,someInt=2347";

    // when
    actualString = myToStringBuilder.toString();

    // then
    assertEquals(expectedEndString, StringUtils.substringBetween(actualString, "[", "]"));
  }

  // Just a micro fixture class for testing.
  private static class FixtureClass {
    String someString;
    int someInt;
  }
}