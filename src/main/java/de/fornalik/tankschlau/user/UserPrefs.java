/*
 * Copyright (c) 2020 Tammo Fornalik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fornalik.tankschlau.user;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.util.StringLegalizer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.PreferenceChangeEvent;
import java.util.prefs.PreferenceChangeListener;
import java.util.prefs.Preferences;

/**
 * This class is responsible for the administration of reading & writing user preferences
 * from & to the local OS user preferences. <br><br>
 * <span style="color:orange;">Important: Each id should be completely lowercase (for
 * Windows OS).</span>
 */
public class UserPrefs implements Serializable {
  private static final Logger LOGGER = Logger.getLogger(UserPrefs.class.getName());
  private static final long serialVersionUID = 1L;

  private static final String ADR_NAME_KEY = "address.name";
  private static final String ADR_STREET_KEY = "address.street";
  private static final String ADR_HOUSENUMBER_KEY = "address.housenumber";
  private static final String ADR_CITY_KEY = "address.city";
  private static final String ADR_POSTCODE_KEY = "address.postcode";
  private static final String GEO_DISTANCE_KEY = "geo.distance";
  private static final String GEO_LATITUDE_KEY = "geo.latitude";
  private static final String GEO_LONGITUDE_KEY = "geo.longitude";
  private static final String PETROL_PREFERRED_TYPE_KEY = "petrol.preferredtype";
  private static final String PETROLSTATIONS_UPDATE_RATE_KEY = "petrolstations.updatecyclerate";
  private static final String MESSAGE_USERID_KEY = "pushmessage.userid";
  private static final String MESSAGE_ENABLED_KEY = "pushmessage.enabled";
  private static final String MESSAGE_MAX_CALLS_UNTIL_SEND_KEY =
      "pushmessage.max_calls_until_force_send";

  private final transient Preferences realPrefs;

  public UserPrefs(String node) {
    this.realPrefs = Preferences.userRoot().node(node);
  }

  public Preferences getRealPrefs() {
    return realPrefs;
  }

  public Optional<Address> readAddress() {
    if (checkPrefsMissing(ADR_STREET_KEY, ADR_CITY_KEY, ADR_POSTCODE_KEY))
      return Optional.empty();

    Address address = new Address(
        realPrefs.get(ADR_NAME_KEY, ""),
        realPrefs.get(ADR_STREET_KEY, ""),
        realPrefs.get(ADR_HOUSENUMBER_KEY, ""),
        realPrefs.get(ADR_CITY_KEY, ""),
        realPrefs.get(ADR_POSTCODE_KEY, ""),
        null);

    readGeo().ifPresent(address::setGeo);

    return Optional.of(address);
  }

  public void writeAddress(Address address) {
    realPrefs.put(ADR_NAME_KEY, address.getName());
    realPrefs.put(ADR_STREET_KEY, address.getStreet());
    realPrefs.put(ADR_HOUSENUMBER_KEY, address.getHouseNumber());
    realPrefs.put(ADR_CITY_KEY, address.getCity());
    realPrefs.put(ADR_POSTCODE_KEY, address.getPostCode());
    address.getGeo().ifPresent(this::writeGeo);
  }

  public Optional<Geo> readGeo() {
    Optional<Geo> geo = readGeoLatLon();

    if (!geo.isPresent())
      return Optional.empty();

    if (checkPrefsMissing(GEO_DISTANCE_KEY))
      return geo;

    geo.get().setDistance(realPrefs.getDouble(GEO_DISTANCE_KEY, -9999.99));

    return geo;
  }

  private Optional<Geo> readGeoLatLon() {
    if (checkPrefsMissing(GEO_LATITUDE_KEY, GEO_LONGITUDE_KEY))
      return Optional.empty();

    double lat = realPrefs.getDouble(GEO_LATITUDE_KEY, -9999.99);
    double lon = realPrefs.getDouble(GEO_LONGITUDE_KEY, -9999.99);

    return Optional.of(new Geo(lat, lon));
  }

  public void writeGeo(Geo geo) {
    realPrefs.putDouble(GEO_LATITUDE_KEY, geo.getLatitude());
    realPrefs.putDouble(GEO_LONGITUDE_KEY, geo.getLongitude());
    geo.getDistance().ifPresent(dist -> realPrefs.putDouble(GEO_DISTANCE_KEY, dist));
  }

  public PetrolType readPreferredPetrolType() {
    checkPrefsMissing(PETROL_PREFERRED_TYPE_KEY);
    String petrolTypeString;

    try {
      petrolTypeString = realPrefs.get(PETROL_PREFERRED_TYPE_KEY, PetrolType.E5.name());
    }
    catch (IllegalStateException e) {
      LOGGER.warning(e.getMessage());
      petrolTypeString = PetrolType.E5.name();
    }

    return PetrolType.valueOf(petrolTypeString);
  }

  public void writePreferredPetrolType(PetrolType type) {
    realPrefs.put(PETROL_PREFERRED_TYPE_KEY, type.toString());
  }

  public int readPetrolStationsUpdateCycleRate() {
    checkPrefsMissing(PETROLSTATIONS_UPDATE_RATE_KEY);
    final int defaultRate = 300;
    int rateSeconds;

    try {
      rateSeconds = realPrefs.getInt(PETROLSTATIONS_UPDATE_RATE_KEY, defaultRate);
    }
    catch (IllegalStateException e) {
      LOGGER.warning(e.getMessage());
      rateSeconds = defaultRate;
    }

    return rateSeconds;
  }

  public void writePetrolStationsUpdateCycleRate(int seconds) {
    if (seconds < 0) return;
    realPrefs.putInt(PETROLSTATIONS_UPDATE_RATE_KEY, seconds);
  }

  public Optional<String> readPushMessageUserId() {
    if (checkPrefsMissing(MESSAGE_USERID_KEY))
      return Optional.empty();

    return Optional.ofNullable(realPrefs.get(MESSAGE_USERID_KEY, null));
  }

  public void writePushMessageUserId(String userId) {
    realPrefs.put(MESSAGE_USERID_KEY, userId);
  }

  public Optional<String> readApiKey(String id) {
    id = StringLegalizer.create(id).mandatory().end();

    if (checkPrefsMissing(id))
      return Optional.empty();

    return Optional.ofNullable(realPrefs.get(id, null));
  }

  // TODO unit tests
  public void writePushMessageEnabled(boolean enable) {
    realPrefs.putBoolean(MESSAGE_ENABLED_KEY, enable);
  }

  // TODO unit tests
  public boolean readPushMessageEnabled() {
    checkPrefsMissing(MESSAGE_ENABLED_KEY);
    return realPrefs.getBoolean(MESSAGE_ENABLED_KEY, false);
  }

  // TODO unit tests
  public void writePushMessageDelayWithNumberOfCalls(int max) {
    if (max < 0) return;
    realPrefs.putInt(MESSAGE_MAX_CALLS_UNTIL_SEND_KEY, max);
  }

  // TODO unit tests
  public int readPushMessageDelayWithNumberOfCalls() {
    checkPrefsMissing(MESSAGE_MAX_CALLS_UNTIL_SEND_KEY);
    return realPrefs.getInt(MESSAGE_MAX_CALLS_UNTIL_SEND_KEY, 20);
  }

  public void writeApiKey(String id, String apiKey) {
    id = StringLegalizer.create(id).mandatory().end();
    apiKey = StringLegalizer.create(apiKey).nullToEmpty().end();

    realPrefs.put(id, apiKey);
  }

  private boolean checkPrefsMissing(String... keysToCheck) {
    Set<String> registeredKeysSet;
    Set<String> keysToCheckSet;

    try {
      if (!realPrefs.nodeExists(""))
        return true;

      registeredKeysSet = new HashSet<>(Arrays.asList(realPrefs.keys()));
      keysToCheckSet = new HashSet<>(Arrays.asList(keysToCheck));
    }
    catch (BackingStoreException e) {
      e.printStackTrace();
      LOGGER.severe("No user preferences found. " + e.getMessage());
      return true;
    }

    return !registeredKeysSet.containsAll(keysToCheckSet);
  }

  // TODO unit tests
  public void registerChangeListener(String forKey, Consumer<String> callback) {
    this.getRealPrefs().addPreferenceChangeListener(new ConsumerChangeListener(forKey, callback));
  }

  // TODO unit tests
  public void registerChangeListener(String forKey, Runnable callback) {
    this.getRealPrefs().addPreferenceChangeListener(new RunnableChangeListener(forKey, callback));
  }

  private static class ConsumerChangeListener extends ChangeListener<Consumer<String>> {

    protected ConsumerChangeListener(String forKey, Consumer<String> callback) {
      super(forKey, callback);
    }

    @Override
    protected void doCallback(PreferenceChangeEvent evt) {
      callback.accept(evt.getNewValue());
    }
  }

  private static class RunnableChangeListener extends ChangeListener<Runnable> {

    protected RunnableChangeListener(String forKey, Runnable callback) {
      super(forKey, callback);
    }

    @Override
    protected void doCallback(PreferenceChangeEvent evt) {
      callback.run();
    }
  }

  private abstract static class ChangeListener<T> implements PreferenceChangeListener {
    protected final String forKey;
    protected final T callback;

    protected ChangeListener(String forKey, T callback) {
      this.forKey = forKey;
      this.callback = callback;
    }

    @Override
    public void preferenceChange(PreferenceChangeEvent evt) {
      if (callback == null || forKey == null)
        return;

      if (evt.getKey().equals(forKey)) {
        doCallback(evt);
      }
    }

    protected abstract void doCallback(PreferenceChangeEvent evt);
  }
}
