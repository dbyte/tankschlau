package de.fornalik.tankschlau.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Locale;
import java.util.ResourceBundle;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocalizationTest {
  private static Localization sutGerman, sutEnglish;

  @BeforeAll
  static void setUpAll() {
    sutGerman = createLocalization(Locale.GERMAN);
    sutEnglish = createLocalization(Locale.ENGLISH);
  }

  @AfterAll
  static void tearDownAll() {
    sutGerman = null;
    sutEnglish = null;
  }

  private static Localization createLocalization(Locale forLocale) {
    // Return a subject under test.
    Localization loc = Localization.newInstance();
    loc.configure(forLocale, ResourceBundle.getBundle("LocaleTestStrings", forLocale));
    return loc;
  }

  @Test
  void get_withoutPlaceholders_returnsExpectedLocalizedTextForEachProvidedLanguage() {
    // given
    String givenKey = "msg.WithoutPlaceholders";
    String actualLocalizedText;

    // when
    actualLocalizedText = sutEnglish.get(givenKey);
    // then
    assertEquals("English message without placeholders.", actualLocalizedText);

    // when
    actualLocalizedText = sutGerman.get(givenKey);
    // then
    assertEquals("Deutsche Nachricht ohne Platzhalter.", actualLocalizedText);
  }

  @Test
  void get_withPlaceholders_returnsExpectedLocalizedTextForEachProvidedLanguage() {
    // given
    String givenKey = "msg.WithPlaceholders";
    String actualLocalizedText;

    // when
    actualLocalizedText = sutEnglish.get(givenKey, "placeholder1", "placeholder2");
    // then
    assertEquals("English message has placeholder1 and placeholder2.", actualLocalizedText);

    // when
    actualLocalizedText = sutGerman.get(givenKey, "Platzhalter1", "Platzhalter2");
    // then
    assertEquals("Deutsche Nachricht hat Platzhalter1 und Platzhalter2.", actualLocalizedText);
  }

  @Test
  void get_throwsNullPointerExceptionOnGivenNullKey() {
    // when then
    assertThrows(NullPointerException.class, () -> sutEnglish.get(null));
    assertThrows(NullPointerException.class, () -> sutGerman.get(null));
  }

  @Test
  void get_returnsErrorStringIfGivenKeyNotExists() {
    // given
    String actualLocalizedText;

    // when
    actualLocalizedText = sutEnglish.get("DoesNotExist");

    // then
    assertEquals(
        "*** Localized string not found for key 'DoesNotExist'. ***",
        actualLocalizedText);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "2,898:2.89756",
      "2,00:2",
      "1,777:1.777",
  }, delimiter = ':')
  void priceFormat_HasMax3AndMin2Fractions(String expectedPriceString, double givenPrice) {
    // given
    String actualPriceString;

    // when
    actualPriceString = sutGerman.priceFormat().format(givenPrice);

    // then
    assertEquals(expectedPriceString, actualPriceString);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "2.00:2.00",
      "1.219:1.219"
  }, delimiter = ':')
  void priceFormat_setsExpectedDecimalSeparatorForLocale(
      String expectedPriceString,
      double givenPrice) {
    // given
    String actualPriceString;

    // when
    actualPriceString = sutEnglish.priceFormat().format(givenPrice);

    // then
    assertEquals(expectedPriceString, actualPriceString);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "2,9:2.89756",
      "2:2.00",
      "1,5:1.5",
      "1,34:1.34",
  }, delimiter = ':')
  void kmFormat_HasMax2AndMin0Fractions(String expectedKmString, double givenKm) {
    // given
    String actualKmString;

    // when
    actualKmString = sutGerman.kmFormat().format(givenKm);

    // then
    assertEquals(expectedKmString, actualKmString);
  }

  @ParameterizedTest
  @CsvSource(value = {
      "2.00:2.00",
      "1.219:1.219"
  }, delimiter = ':')
  void kmFormat_setsExpectedDecimalSeparatorForLocale(String expectedKmString, double givenKm) {
    // given
    String actualKmString;

    // when
    actualKmString = sutEnglish.priceFormat().format(givenKm);

    // then
    assertEquals(expectedKmString, actualKmString);
  }
}