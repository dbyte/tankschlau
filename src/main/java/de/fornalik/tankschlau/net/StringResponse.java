package de.fornalik.tankschlau.net;

import java.util.Optional;

/**
 * Implementation for server responses which provide a somewhat textual format. Might be JSON, too.
 */
public class StringResponse implements Response {
  private String body;
  private String errorMessage;

  private StringResponse() {}

  /**
   * Default factory. Creates a ready-for-use Response.
   */
  public static StringResponse create() {
    return new StringResponse();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<String> getBody() {
    return Optional.ofNullable(body);
  }

  @Override
  public <T> void setBody(T data) {
    if (!(data instanceof String))
      throw new ClassCastException("Data must be of type String.");

    this.body = (String) data;
  }

  @Override
  public Optional<String> getErrorMessage() {
    return Optional.ofNullable(errorMessage);
  }

  @Override
  public void setErrorMessage(String in) {
    this.errorMessage = in;
  }
}
