package de.fornalik.tankschlau.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * Utility class for predefined localized strings.
 */
public class Localization {
  private final ResourceBundle bundle;

  public Localization(Locale locale) {
    this.bundle = ResourceBundle.getBundle("LocaleStrings", locale);
  }

  public Localization() {
    this(Locale.GERMAN);
  }

  /**
   * Gets the locale string, depending on language settings.
   *
   * @param key  Unique key (residing in ResourceBundle.properties) to identify the string we
   *             want to get.
   * @param args Placeholder(s) to insert into the string or {@code null} if no placeholders
   *             are provided.
   * @return Localized string, depending on language settings.
   */
  public String get(String key, Object... args) {
    Objects.requireNonNull(key, "Parameter 'key' must not be null.");

    String pattern = bundle.getString(key);

    if (args == null)
      return pattern;

    return MessageFormat.format(pattern, args);
  }

  /**
   * Gets the locale string, depending on language settings.
   * Simple version of {@link #get(String, Object...)}, without placeholders.
   *
   * @param key see {@link #get(String, Object...)}
   * @return Localized string, depending on language settings.
   */
  public String get(String key) {
    return get(key, (Object) null);
  }
}