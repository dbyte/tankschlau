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

package de.fornalik.tankschlau.util;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * This class is responsible for the administration of reading & writing user preferences
 * from & to the local OS user preferences.
 */
public class UserPrefs {
  private final Preferences realPrefs;

  public UserPrefs() {
    this(UserPrefs.class);
  }

  public UserPrefs(Class<?> clazz) {
    this.realPrefs = Preferences.userNodeForPackage(clazz);
  }

  public Preferences getRealPrefs() {
    return realPrefs;
  }

  public Optional<Address> readAddress() {
    if (checkPrefsMissing("address.street", "address.city", "address.postCode"))
      return Optional.empty();

    Address address = new Address(
        realPrefs.get("address.name", ""),
        realPrefs.get("address.street", ""),
        realPrefs.get("address.houseNumber", ""),
        realPrefs.get("address.city", ""),
        realPrefs.get("address.postCode", ""),
        null);

    readGeo().ifPresent(address::setGeo);

    return Optional.of(address);
  }

  public void writeAddress(Address address) {
    realPrefs.put("address.name", address.getName());
    realPrefs.put("address.street", address.getStreet());
    realPrefs.put("address.houseNumber", address.getHouseNumber());
    realPrefs.put("address.city", address.getCity());
    realPrefs.put("address.postCode", address.getPostCode());
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

  public Optional<PetrolType> readPreferredPetrolType() {
    if (checkPrefsMissing("petrol.preferredType"))
      return Optional.empty();

    String petrolTypeString = realPrefs.get("petrol.preferredType", null);
    return Optional.of(PetrolType.valueOf(PetrolType.class, petrolTypeString));
  }

  public void writePreferredPetrolType(PetrolType type) {
    realPrefs.put("petrol.preferredType", type.toString());
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
      return true;
    }

    return !registeredKeysSet.containsAll(keysToCheckSet);
  }
}
