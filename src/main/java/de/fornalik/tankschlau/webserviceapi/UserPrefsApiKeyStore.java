package de.fornalik.tankschlau.webserviceapi;

import de.fornalik.tankschlau.util.UserPrefs;

import java.util.Optional;

// TODO unit tests
/**
 * Implementation for {@link UserPrefs} based API key storage.
 *
 * @implNote Note this implementation is a <span style="color:red;">security risk</span>
 * and therefore should only be used for demo and testing purposes.
 */
public class UserPrefsApiKeyStore implements ApiKeyStore {
  private final UserPrefs userPrefs;

  public UserPrefsApiKeyStore(UserPrefs userPrefs) {
    this.userPrefs = userPrefs;
  }

  @Override
  public Optional<String> read(String id) {
    return userPrefs.readApiKey(id);
  }

  @Override
  public void write(String id, String apiKey) {
    userPrefs.writeApiKey(id, apiKey);
  }
}
