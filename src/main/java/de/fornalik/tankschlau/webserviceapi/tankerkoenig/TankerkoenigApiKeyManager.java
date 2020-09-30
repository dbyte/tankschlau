package de.fornalik.tankschlau.webserviceapi.tankerkoenig;

import de.fornalik.tankschlau.webserviceapi.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.ApiKeyStore;

public class TankerkoenigApiKeyManager extends ApiKeyManager {

  public TankerkoenigApiKeyManager(ApiKeyStore apiKeyStore) {
    super(apiKeyStore, "apiKey.tankerkoenig");
  }

  /**
   * This is a fixed demo API key, provided by API provider tankerkoenig.de.
   * Note that all returned data requested with that key are just example data with equal prices.
   */
  @Override
  public String readDemoKey() {
    return "00000000-0000-0000-0000-000000000002";
  }
}
