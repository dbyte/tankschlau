package de.fornalik.tankschlau.webserviceapi;

import de.fornalik.tankschlau.util.StringLegalizer;

import java.util.Objects;
import java.util.Optional;

public abstract class ApiKeyManager {
  protected final String id;
  protected final ApiKeyStore apiKeyStore;

  protected ApiKeyManager(ApiKeyStore apiKeyStore, String id) {
    this.apiKeyStore = Objects.requireNonNull(apiKeyStore);
    this.id = StringLegalizer.create(id).mandatory().end();
  }

  public Optional<String> read() {
    return apiKeyStore.read(id);
  }

  public abstract String readDemoKey();

  public void write(String apiKey) {
    apiKeyStore.write(id, StringLegalizer.create(id).nullToEmpty().end());
  }
}
