package de.fornalik.tankschlau.webserviceapi;

import de.fornalik.tankschlau.util.StringLegalizer;

import java.util.Objects;
import java.util.Optional;

// TODO unit tests

/**
 * Abstract base class for API key handling.
 */
public abstract class ApiKeyManager {
  protected final String id;
  protected final ApiKeyStore apiKeyStore;

  /**
   * Constructor
   *
   * @param apiKeyStore Storage strategy for the API key.
   * @param id          Unique identifier for the API key within a sort of data collection.
   */
  protected ApiKeyManager(ApiKeyStore apiKeyStore, String id) {
    this.apiKeyStore = Objects.requireNonNull(apiKeyStore);
    this.id = StringLegalizer.create(id).mandatory().end();
  }

  /**
   * Reads the API key from storage.
   *
   * @return The API key itself as an Optional, or an empty Optional if the key not exists in
   * storage.
   */
  public Optional<String> read() {
    return apiKeyStore.read(id);
  }

  /**
   * Returns a demo key for the API. Can be used if no API key was set in storage yet.
   *
   * @return The demo API key.
   */
  public abstract String readDemoKey();

  /**
   * Writes a given API key into the storage.
   *
   * @param apiKey The API key to write into the storage.
   */
  public void write(String apiKey) {
    apiKeyStore.write(id, StringLegalizer.create(apiKey).nullToEmpty().end());
  }
}
