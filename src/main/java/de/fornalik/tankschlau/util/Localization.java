package de.fornalik.tankschlau.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * ResourceBundle/Locale facade for localized strings. This is meant to be handled like a
 * Singleton - instantiate a single object for the lifecycle of the application.
 */
public class Localization {
  private final ResourceBundle bundle;

  public Localization(Locale locale) {
    this.bundle = ResourceBundle.getBundle("LocaleStrings", locale);
  }

  /**
   * Gets the locale string, depending on {@link Locale} language settings.
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
   * Gets the locale string, depending on {@link Locale} language settings.
   * Simple version of {@link #get(String, Object...)}, without placeholders.
   *
   * @param key see {@link #get(String, Object...)}
   * @return Localized string, depending on language settings.
   */
  public String get(String key) {
    return get(key, (Object) null);
  }
}