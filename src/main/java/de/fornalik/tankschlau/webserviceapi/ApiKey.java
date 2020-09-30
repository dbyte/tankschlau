package de.fornalik.tankschlau.webserviceapi;

/**
 * Interface for web service API key implementations.
 */
interface ApiKey {

  /**
   * Reads an API key.
   *
   * @param which A token to identify which API key to return.
   * @return Decrypted API key
   */
  String read(String which);

  /**
   * Writes an API, which should be encrypted by the implementation.
   *
   * @param which  A token to identify which API key to write.
   * @param apiKey The API key
   */
  void write(String which, String apiKey);
}
