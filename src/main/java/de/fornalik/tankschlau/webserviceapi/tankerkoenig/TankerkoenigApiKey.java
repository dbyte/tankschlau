package de.fornalik.tankschlau.webserviceapi.tankerkoenig;

import de.fornalik.tankschlau.webserviceapi.ApiKeyStore;

import java.util.Optional;

public class TankerkoenigApiKey {
  private static final String IDENTIFIER = "apiKey.tankerkoenig";
  ApiKeyStore apiKeyStore;

  public TankerkoenigApiKey(ApiKeyStore apiKeyStore) {
    this.apiKeyStore = apiKeyStore;
  }

  public Optional<String> read() {
    return apiKeyStore.read(IDENTIFIER);
  }

  public void write(String apiKey) {
    apiKeyStore.write(IDENTIFIER, apiKey);
  }
}
