package de.fornalik.tankschlau.net;

import java.util.Optional;

/**
 * The HTTP response interface used by this application.
 */
public interface Response {

  /**
   * @return Optional error message for errors which have not been thrown at request time.
   * Empty Optional if no errors were detected.
   */
  Optional<String> getErrorMessage();

  /**
   * @param message Sets an error message for errors which have not been thrown at request time.
   *                Do not set at all (or null) if no error were encountered.
   */
  void setErrorMessage(String message);

  /**
   * Gets the final response body data as a string.
   *
   * @return Optional body of the response as string, e.g. a JSON string. Or an empty Optional
   * if there were errors which have not been thrown at request time - in this case,
   * {@link #getErrorMessage()} should return the message which prevented the body from
   * being converted to string.
   */
  Optional<String> getBodyString();

  /**
   * Sets the final string data.
   *
   * @param data Some string
   */
  void setBodyString(String data);
}
