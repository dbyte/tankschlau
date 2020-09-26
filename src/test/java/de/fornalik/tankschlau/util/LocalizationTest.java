package de.fornalik.tankschlau.util;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

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
    String baseName = "LocaleTestStrings";
    return new Localization(ResourceBundle.getBundle(baseName, forLocale));
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
}