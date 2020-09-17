package de.fornalik.tankschlau.net;

import java.util.Optional;

public class ResponseImpl implements Response {
  private String bodyString;
  private String errorMessage;

  private ResponseImpl() {
  }

  /**
   * Default factory which creates a ready-for-use Response.
   */
  public static Response create() {
    return new ResponseImpl();
  }

  @Override
  public Optional<String> getBodyString() {
    return Optional.ofNullable(bodyString);
  }

  @Override
  public void setBodyString(String in) {
    this.bodyString = in;
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
