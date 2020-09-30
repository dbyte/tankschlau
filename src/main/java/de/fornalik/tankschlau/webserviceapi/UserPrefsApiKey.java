package de.fornalik.tankschlau.webserviceapi;

import de.fornalik.tankschlau.util.UserPrefs;

import java.util.Optional;

/**
 * Implementation for API keys which access {@link UserPrefs} to read and write encrypted API keys.
 *
 * @implNote Note this implementation is a <span style="color:red;">security risk</span>
 * in spite of using crypto, and therefore should only be used for demo and testing purposes.
 */
public class UserPrefsApiKey implements ApiKey {
  private final UserPrefs userPrefs;

  public UserPrefsApiKey(UserPrefs userPrefs) {
    this.userPrefs = userPrefs;
  }

  @Override
  public Optional<String> read(String userPrefKey) {
    return userPrefs.readEncryptedApiKey(userPrefKey).map(this::decrypt);
  }

  @Override
  public void write(String userPrefKey, String apiKey) {
    String encrypted = encrypt(apiKey);

    // Try clearing memory for unencrypted key as soon as possible
    // noinspection UnusedAssignment
    apiKey = null;

    userPrefs.writeEncryptedApiKey(userPrefKey, encrypted);
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
