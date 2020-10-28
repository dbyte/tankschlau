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
public class UserPrefs {
  private static final Logger LOGGER = Logger.getLogger(UserPrefs.class.getName());
  private final Preferences realPrefs;

  public UserPrefs(String node) {
    this.realPrefs = Preferences.userRoot().node(node);
  }

  public Preferences getRealPrefs() {
    return realPrefs;
  }

  public Optional<Address> readAddress() {
    if (checkPrefsMissing("address.street", "address.city", "address.postcode"))
      return Optional.empty();

    Address address = new Address(
        realPrefs.get("address.name", ""),
        realPrefs.get("address.street", ""),
        realPrefs.get("address.housenumber", ""),
        realPrefs.get("address.city", ""),
        realPrefs.get("address.postcode", ""),
        null);

    readGeo().ifPresent(address::setGeo);

    return Optional.of(address);
  }

  public void writeAddress(Address address) {
    realPrefs.put("address.name", address.getName());
    realPrefs.put("address.street", address.getStreet());
    realPrefs.put("address.housenumber", address.getHouseNumber());
    realPrefs.put("address.city", address.getCity());
    realPrefs.put("address.postcode", address.getPostCode());
    address.getGeo().ifPresent(this::writeGeo);
  }

  public Optional<Geo> readGeo() {
    Optional<Geo> geo = readGeoLatLon();

    if (!geo.isPresent())
      return Optional.empty();

    if (checkPrefsMissing("geo.distance"))
      return geo;

    geo.get().setDistance(realPrefs.getDouble("geo.distance", -9999.99));

    return geo;
  }

  private Optional<Geo> readGeoLatLon() {
    if (checkPrefsMissing("geo.latitude", "geo.longitude"))
      return Optional.empty();

    double lat = realPrefs.getDouble("geo.latitude", -9999.99);
    double lon = realPrefs.getDouble("geo.longitude", -9999.99);

    return Optional.of(new Geo(lat, lon));
  }

  public void writeGeo(Geo geo) {
    realPrefs.putDouble("geo.latitude", geo.getLatitude());
    realPrefs.putDouble("geo.longitude", geo.getLongitude());
    geo.getDistance().ifPresent(dist -> realPrefs.putDouble("geo.distance", dist));
  }

  public PetrolType readPreferredPetrolType() {
    checkPrefsMissing("petrol.preferredtype");
    String petrolTypeString;

    try {
      petrolTypeString = realPrefs.get("petrol.preferredtype", PetrolType.E5.name());
    }
    catch (IllegalStateException e) {
      LOGGER.warning(e.getMessage());
      petrolTypeString = PetrolType.E5.name();
    }

    return PetrolType.valueOf(PetrolType.class, petrolTypeString);
  }

  public void writePreferredPetrolType(PetrolType type) {
    realPrefs.put("petrol.preferredtype", type.toString());
  }

  public int readPetrolStationsUpdateCycleRate() {
    checkPrefsMissing("petrolstations.updatecyclerate");
    final int defaultRate = 300;
    int rateSeconds;

    try {
      rateSeconds = realPrefs.getInt("petrolstations.updatecyclerate", defaultRate);
    }
    catch (IllegalStateException e) {
      LOGGER.warning(e.getMessage());
      rateSeconds = defaultRate;
    }

    return rateSeconds;
  }

  public void writePetrolStationsUpdateCycleRate(int seconds) {
    if (seconds < 0) return;
    realPrefs.putInt("petrolstations.updatecyclerate", seconds);
  }

  public Optional<String> readPushMessageUserId() {
    String key = "pushmessage.userid";

    if (checkPrefsMissing(key))
      return Optional.empty();

    return Optional.ofNullable(realPrefs.get(key, null));
  }

  public void writePushMessageUserId(String userId) {
    realPrefs.put("pushmessage.userid", userId);
  }

  public Optional<String> readApiKey(String id) {
    id = StringLegalizer.create(id).mandatory().end();

    if (checkPrefsMissing(id))
      return Optional.empty();

    return Optional.ofNullable(realPrefs.get(id, null));
  }

  public void writeApiKey(String id, String apiKey) {
    id = StringLegalizer.create(id).mandatory().end();
    apiKey = StringLegalizer.create(apiKey).mandatory().end();

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

  private static abstract class ChangeListener<T> implements PreferenceChangeListener {
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
