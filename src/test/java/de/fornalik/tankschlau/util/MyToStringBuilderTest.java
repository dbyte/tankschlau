package de.fornalik.tankschlau.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MyToStringBuilderTest {
  private MyToStringBuilder sut;
  private FixtureClass givenInstance;
  private String expectedString, actualString;

  @BeforeEach
  void setUp() {
    givenInstance = new FixtureClass();
    sut = new MyToStringBuilder(givenInstance);
    expectedString = null;
    actualString = null;
  }

  @Test
  void toString_producesImplicitShortClassNameStyle() {
    // given
    givenInstance.someString = "This is a test value.";
    expectedString = "MyToStringBuilderTest.FixtureClass@";
    sut.append("someString", givenInstance.someString);

    // when
    actualString = sut.toString();

    // then
    assertTrue(
        actualString.startsWith(expectedString),
        "Expected short name style string must start with ==>\n"
            + expectedString + "\n"
            + "but actually is ==>\n"
            + actualString);
  }

  @Test
  void toString_producesExpectedStringForFields() {
    // given
    givenInstance.someString = "This is another test value ÖÄÜ.";
    givenInstance.someInt = 2347;
    expectedString = "[someString=This is another test value ÖÄÜ.,someInt=2347]";

    sut.append("someString", givenInstance.someString);
    sut.append("someInt", givenInstance.someInt);

    // when
    actualString = sut.toString();

    // then
    assertTrue(
        actualString.contains(expectedString),
        "Expected short name style string must contain ==>\n"
            + expectedString + "\n"
            + "but actually is ==>\n"
            + actualString);
  }

  // Just a micro fixture for testing.
  private static class FixtureClass {
    String someString;
    int someInt;
  }
}