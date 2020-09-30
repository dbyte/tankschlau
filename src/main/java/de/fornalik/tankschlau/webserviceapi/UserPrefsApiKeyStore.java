package de.fornalik.tankschlau.webserviceapi;

import de.fornalik.tankschlau.util.UserPrefs;

import java.util.Optional;

/**
 * Implementation for {@link UserPrefs} based API key storage.
 *
 * @implNote Note this implementation is a <span style="color:red;">security risk</span>
 * in spite of using crypto, and therefore should only be used for demo and testing purposes.
 */
public class UserPrefsApiKeyStore implements ApiKeyStore {
  private final UserPrefs userPrefs;

  public UserPrefsApiKeyStore(UserPrefs userPrefs) {
    this.userPrefs = userPrefs;
  }

  @Override
  public Optional<String> read(String id) {
    return userPrefs.readEncryptedApiKey(id).map(this::decrypt);
  }

  @Override
  public void write(String id, String apiKey) {
    String encrypted = encrypt(apiKey);

    // Try clearing memory for unencrypted key as soon as possible
    // noinspection UnusedAssignment
    apiKey = null;

    userPrefs.writeEncryptedApiKey(id, encrypted);
  }

  // TODO
  private String encrypt(String apiKey) {
    return apiKey;
  }

  // TODO
  private String decrypt(String apiKey) {
    return apiKey;
  }
}
