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
import java.text.NumberFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * ResourceBundle/Locale wrapper for localized strings and region formats. Singleton.
 */
public class Localization {
  private static Localization INSTANCE;
  private ResourceBundle bundle;
  private Locale locale;

  private Localization() {}

  /**
   * @return Singleton - defaults to our custom german language/format settings at creation time.
   * @see #newInstance()
   */
  public static Localization getInstance() {
    if (INSTANCE != null)
      return INSTANCE;

    INSTANCE = newInstance();
    return INSTANCE;
  }

  /**
   * Unlike {@link #getInstance()}, this factory does <b>NOT</b> return the singleton.
   * <p>
   * Defaults to our custom german settings and language bundle "/resources/LocaleStrings".
   * To change these settings afterwards, call {@link #configure(Locale)} or
   * {@link #configure(Locale, ResourceBundle)}.
   *
   * @see #getInstance()
   */
  public static synchronized Localization newInstance() {
    Localization loc = new Localization();
    loc.configure(Locale.GERMANY);
    return loc;
  }

  /**
   * Configure app for the given language/region. Implicitly sets path to production bundle of
   * language properties files.
   *
   * @param locale App language and formats.
   */
  public void configure(Locale locale) {
    this.configure(locale, ResourceBundle.getBundle("LocaleStrings", locale));
  }

  /**
   * Configure app for the given language/region. Unlike {@link #configure(Locale)}, this one
   * does NOT implicitly set the path to production bundle of language properties files.
   *
   * @param bundle Target {@link ResourceBundle} for the language property files.
   * @param locale Region for specific formats we use.
   */
  public void configure(Locale locale, ResourceBundle bundle) {
    this.bundle = bundle;
    this.locale = locale;
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

  /**
   * @return The application-wide price formatter. Decimal separator depending on Locale settings.
   */
  public NumberFormat priceFormat() {
    NumberFormat nf = NumberFormat.getInstance(locale);
    nf.setMaximumFractionDigits(3);
    nf.setMinimumFractionDigits(2);
    return nf;
  }

  /**
   * @return The application-wide kilometers formatter. Decimal separator depending on Locale
   * settings.
   */
  public NumberFormat kmFormat() {
    NumberFormat nf = NumberFormat.getInstance(locale);
    nf.setMaximumFractionDigits(2);
    nf.setMinimumFractionDigits(0);
    return nf;
  }
}