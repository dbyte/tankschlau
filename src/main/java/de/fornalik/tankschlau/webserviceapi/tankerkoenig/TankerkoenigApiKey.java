package de.fornalik.tankschlau.webserviceapi.tankerkoenig;

import de.fornalik.tankschlau.webserviceapi.ApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.BaseApiKey;

public class TankerkoenigApiKey extends BaseApiKey {

  public TankerkoenigApiKey(ApiKeyStore apiKeyStore) {
    super(apiKeyStore, "apiKey.tankerkoenig");
  }
}
