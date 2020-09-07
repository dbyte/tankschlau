package de.fornalik.tankschlau.util;

import java.util.Optional;

public class StringLegalizer {
  private String string;

  private StringLegalizer(String string) {
    this.string = string;
  }

  public static StringLegalizer init(String s) {
    return new StringLegalizer(s);
  }

  public StringLegalizer safeTrim() {
    string = Optional.ofNullable(string)
        .map(String::trim)
        .orElse(string);

    return this;
  }

  public StringLegalizer mandatory() throws ValueException {
    if (string == null || string.isEmpty())
      throw new ValueException("String must not be null or empty.");

    return this;
  }

  public StringLegalizer nullToEmpty() {
    if (string != null) return this;

    string = "";
    return this;
  }

  public String end() {
    return string;
  }

  public static class ValueException extends Exception {
    public ValueException(String message) {
      super(message);
    }
  }
}
