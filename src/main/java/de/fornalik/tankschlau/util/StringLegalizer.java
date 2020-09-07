package de.fornalik.tankschlau.util;

import java.util.Optional;

/**
 * Use to verify/legalize a String, using method chaining.
 */
public class StringLegalizer {
  private String string;

  private StringLegalizer(String string) {
    this.string = string;
  }

  /**
   * Use to start a new string verification/legalization.
   * After chaining one ore more methods of this class, call {@code end()}
   *
   * @param s The String to legalize/validate
   * @return A new instance of StringLegalizer for method chaining.
   */
  public static StringLegalizer init(String s) {
    return new StringLegalizer(s);
  }

  /**
   * Use to strip whitespace at start and end of a String.
   * If the given String is null, nothing is executed.
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
   * Finalize legalization and return the final String.
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
