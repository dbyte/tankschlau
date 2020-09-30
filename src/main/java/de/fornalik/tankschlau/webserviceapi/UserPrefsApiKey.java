package de.fornalik.tankschlau.webserviceapi;

/**
 * Implementation for API keys which access {@link de.fornalik.tankschlau.util.UserPrefs}
 * to read and write encrypted API keys.
 * Note this implementation is a security risk and should only be used
 */
public abstract class UserPrefsApiKey implements ApiKey {

  @Override
  public String read(String userPrefKey) {
    return null;
  }

  @Override
  public void write(String userPrefKey, String apiKey) {

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
