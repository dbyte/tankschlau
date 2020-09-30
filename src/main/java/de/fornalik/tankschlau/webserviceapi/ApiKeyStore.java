package de.fornalik.tankschlau.webserviceapi;

import java.util.Optional;

/**
 * Interface for API key storage implementations.
 */
public interface ApiKeyStore {

  /**
   * Reads an API key from the store.
   *
   * @param id A token to identify which API key to return from the storage.
   * @return Decrypted value of the API key
   */
  Optional<String> read(String id);

  /**
   * Writes an API key (which should be encrypted by implementation) to the store.
   *
   * @param id     A token to identify which API key to write into the storage.
   * @param apiKey The value of the API key
   */
  void write(String id, String apiKey);
}
