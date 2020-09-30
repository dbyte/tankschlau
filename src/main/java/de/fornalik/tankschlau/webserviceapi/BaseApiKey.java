package de.fornalik.tankschlau.webserviceapi;

import de.fornalik.tankschlau.util.StringLegalizer;

import java.util.Objects;
import java.util.Optional;

public abstract class BaseApiKey {
  protected final String id;
  protected final ApiKeyStore apiKeyStore;

  protected BaseApiKey(ApiKeyStore apiKeyStore, String id) {
    this.apiKeyStore = Objects.requireNonNull(apiKeyStore);
    this.id = StringLegalizer.create(id).mandatory().end();
  }

  protected Optional<String> read() {
    return apiKeyStore.read(id);
  }

  protected void write(String apiKey) {
    apiKeyStore.write(id, StringLegalizer.create(id).nullToEmpty().end());
  }
}
