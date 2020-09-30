package de.fornalik.tankschlau.webserviceapi.tankerkoenig;

import de.fornalik.tankschlau.webserviceapi.ApiKey;

import java.util.Optional;

public class TankerkoenigApiKey {
  private static final String IDENTIFIER = "apiKey.tankerkoenig";
  ApiKey apiKeyProvider;

  public TankerkoenigApiKey(ApiKey apiKeyProvider) {
    this.apiKeyProvider = apiKeyProvider;
  }

  public Optional<String> read() {
    return apiKeyProvider.read(IDENTIFIER);
  }

  public void write(String apiKey) {
    apiKeyProvider.write(IDENTIFIER, apiKey);
  }
}
