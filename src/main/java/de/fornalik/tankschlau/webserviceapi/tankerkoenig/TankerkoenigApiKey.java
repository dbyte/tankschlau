package de.fornalik.tankschlau.webserviceapi.tankerkoenig;

import de.fornalik.tankschlau.webserviceapi.ApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.BaseApiKey;

public class TankerkoenigApiKey extends BaseApiKey {

  public TankerkoenigApiKey(ApiKeyStore apiKeyStore) {
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
