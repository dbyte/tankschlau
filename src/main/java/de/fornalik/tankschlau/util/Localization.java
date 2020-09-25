package de.fornalik.tankschlau.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Utility class for predefined localized strings.
 */
public class Localization {
  private static final Locale locale = Locale.GERMAN;
  private static final ResourceBundle bundle = ResourceBundle
      .getBundle("LocaleStrings", locale);

  /**
   * @param key  Unique key (residing in ResourceBundle.properties) to identify the string we
   *             want to get.
   * @param args Placeholder(s) to insert into the string or {@code null} if no placeholder
   *             are provided.
   * @return Localized string, depending on language settings.
   */
  public static String get(String key, Object... args) {
    Objects.requireNonNull(key, "key must not be null.");

    String pattern = bundle.getString(key);

    if (args == null)
      return pattern;

    return MessageFormat.format(pattern, args);
  }

  /**
   * @param key see {@link #get(String, Object...)}
   * @return Localized string, depending on language settings.
   */
  public static String get(String key) {
    return Localization.get(key, (Object) null);
  }
}