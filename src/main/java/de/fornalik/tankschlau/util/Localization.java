/*
 * Copyright (c) 2020 Tammo Fornalik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fornalik.tankschlau.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * ResourceBundle/Locale facade for localized strings. This is meant to be handled like a
 * Singleton - instantiate a single object for the lifecycle of the application.
 */
public class Localization {
  private final ResourceBundle bundle;

  /**
   * Configure app for the given language/region, which have been defined within the given
   * {@link ResourceBundle}.
   *
   * @param bundle Target {@link ResourceBundle} enclosing the property files
   * @see #Localization(Locale)
   */
  public Localization(ResourceBundle bundle) {
    this.bundle = bundle;
  }

  /**
   * Configure app for the given language/region. The implicit {@link ResourceBundle} bundle
   * for this constructor is "/resources/LocaleStrings". Take the overloaded constructor to
   * explicitly set a different bundle.
   *
   * @param locale language token, ex. use {@link Locale#GERMAN}
   * @see #Localization(ResourceBundle)
   */
  public Localization(Locale locale) {
    this(ResourceBundle.getBundle("LocaleStrings", locale));
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
    String pattern;

    try {
      pattern = bundle.getString(key);
    }
    catch (MissingResourceException e) {
      return "*** Localized string not found for key '" + key + "' ***";
    }

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