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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

/**
 * Utility class to verify/legalize a String, using method chaining.
 */
public class StringLegalizer {
  private String string;

  private StringLegalizer(String string) {
    this.string = string;
  }

  /**
   * Static factory, use to start a new string verification/legalization.
   * After chaining one ore more methods of this class, call {@code end()}
   *
   * @param s The String to legalize/validate
   * @return A new instance of StringLegalizer for method chaining.
   */
  public static StringLegalizer create(String s) {
    return new StringLegalizer(s);
  }

  public String getString() {
    return string;
  }

  /**
   * Use to strip whitespace at start and end of a String.
   * If the given String is null, nothing is executed and the string remains null.
   *
   * @return Trimmed String
   */
  public StringLegalizer safeTrim() {
    string = Optional.ofNullable(string)
        .map(String::trim)
        .orElse(string);

    return this;
  }

  /**
   * Validates if the given String is not null and not empty.
   * If the validation fails, an unchecked StringLegalizer.ValueException is thrown.
   * Use for setters etc.
   *
   * @return Instance of StringLegalizer for fluid usage if String is valid.
   * @throws ValueException If String is null or empty.
   */
  public StringLegalizer mandatory() throws ValueException {
    boolean isValid = string != null && !string.isEmpty();
    if (isValid) return this;

    String state = string == null ? "null" : "empty";
    throw new ValueException(String.format("Mandatory string must not be %s.", state));
  }

  public StringLegalizer nullToEmpty() {
    if (string != null) return this;

    string = "";
    return this;
  }

  /**
   * Convert a string to a {@link URL} by replacing the checked {@link MalformedURLException} to an
   * unchecked {@link RuntimeException} when we don't want a bubbling up exception invasion.
   *
   * @return A validated {@link URL}.
   * @throws RuntimeException If the string contains a malformed URL.
   */
  public URL toUrl() {
    try {
      return new URL(string);
    }
    catch (MalformedURLException e) {
      throw new RuntimeException("Malformed URL: " + string, e);
    }
  }

  /**
   * Finalize legalization and return the final String.
   *
   * @return The final validated/legalized String.
   */
  public String end() {
    return string;
  }

  public static class ValueException extends RuntimeException {
    public ValueException(String message) {
      super(message);
    }
  }
}
