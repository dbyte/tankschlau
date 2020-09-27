package de.fornalik.tankschlau.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StringLegalizerTest {
  StringLegalizer legalizer;
  private String givenString;

  @BeforeEach
  void setUp() {
    givenString = null;
    legalizer = null;
  }

  @Test
  void create_returnsNewStringLegalizerWithGivenString() {
    // given
    givenString = "This is the given string value.";

    // when
    legalizer = StringLegalizer.create(givenString);

    // then
    assertNotNull(legalizer);
    assertEquals(givenString, legalizer.getString());
  }

  @Test
  void safeTrim_leavesGivenStringUntouchedIfThereIsNothingToTrim() {
    // given
    givenString = "Nothing to trim here";
    legalizer = StringLegalizer.create(givenString);

    // when
    legalizer = legalizer.safeTrim();

    // then
    assertEquals(givenString, legalizer.getString());
  }

  @Test
  void safeTrim_trimsOutWhitespaceProperly() {
    // given
    givenString = "   \nSome whitespace before and after \n\r  ";
    legalizer = StringLegalizer.create(givenString);

    // when
    legalizer = legalizer.safeTrim();

    // then
    assertEquals("Some whitespace before and after", legalizer.getString());
  }

  @Test
  void safeTrim_doesNotThrowIfGivenStringIsNull() {
    // given
    legalizer = StringLegalizer.create(null);

    // when then
    assertDoesNotThrow(() -> legalizer.safeTrim());
  }

  @Test
  void safeTrim_leavesStringUntouchedIfGivenStringIsNull() {
    // given
    legalizer = StringLegalizer.create(null);

    // when
    legalizer = legalizer.safeTrim();

    // then
    assertNull(legalizer.getString());
  }

  @Test
  void mandatory_throwsIfGivenStringIsNull() {
    // given
    legalizer = StringLegalizer.create(null);

    // when then
    assertThrows(StringLegalizer.ValueException.class, () -> legalizer.mandatory());
  }

  @Test
  void mandatory_throwsIfGivenStringIsEmpty() {
    // given
    givenString = "";
    legalizer = StringLegalizer.create(givenString);

    // when then
    assertThrows(StringLegalizer.ValueException.class, () -> legalizer.mandatory());
  }

  @Test
  void mandatory_doesNotThrowIfGivenStringIsNotEmpty() {
    // given
    givenString = "This is a non empty string";
    legalizer = StringLegalizer.create(givenString);

    // when then
    assertDoesNotThrow(() -> legalizer.mandatory());
  }

  @Test
  void nullToEmpty_resultsInModifyingNullStringToEmptyString() {
    // given
    legalizer = StringLegalizer.create(null);

    // when
    legalizer = legalizer.nullToEmpty();

    // then
    assertEquals("", legalizer.getString());
  }

  @Test
  void end_returnsTheLegalizedString() {
    // given
    givenString = "Expected to be the returned string";
    legalizer = StringLegalizer.create(givenString);

    // when
    String actualReturnedString = legalizer.safeTrim().end();

    // then
    assertEquals("Expected to be the returned string", actualReturnedString);
  }
}