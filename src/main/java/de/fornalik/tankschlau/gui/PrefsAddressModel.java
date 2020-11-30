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

package de.fornalik.tankschlau.gui;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.service.GeocodingService;
import de.fornalik.tankschlau.service.GeocodingWorker;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.WorkerService;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

@Component
class PrefsAddressModel {

  private final WorkerService<Geo> workerService;
  private final UserPrefs userPrefs;

  @Autowired
  PrefsAddressModel(WorkerService<Geo> workerService, UserPrefs userPrefs) {
    this.workerService = workerService;
    this.userPrefs = userPrefs;
  }

  void getGeoDataForAddress(Consumer<Geo> geoDataConsumerCallback, Map<String, String> addressMap) {
    // Get Geo data (latitude, longitude) from a web service by the provided address.
    getGeocodingWorker().setUserAddress(createAddressFromMap(addressMap));
    workerService.startOneShot(geoDataConsumerCallback);
  }

  boolean isGeoServiceGoogleGeocodingImplementation() {
    return (getGeocodingService().getClass() == GoogleGeocodingClient.class);
  }

  Optional<Address> readAddressFromUserPrefs() {
    return userPrefs.readAddress();
  }

  void writeAddressToUserPrefs(Map<String, String> map) {
    userPrefs.writeAddress(createAddressFromMap(map));
  }

  Optional<Geo> readGeoFromUserPrefs() {
    return userPrefs.readGeo();
  }

  void writeGeoToUserPrefs(Map<String, String> map) {
    try {
      userPrefs.writeGeo(createGeoFromMap(map));
    }
    catch (GeoDataParsingException ex) {
      // Ignore, as address without Geo data is perfectly valid.
    }
  }

  private Address createAddressFromMap(Map<String, String> map) {
    Address address = new Address(
        map.get("street"),
        map.get("houseNumber"),
        map.get("city"),
        map.get("postCode")
    );

    try {
      address.setGeo(createGeoFromMap(map));
    }
    catch (GeoDataParsingException ex) {
      // Ignore, as address without Geo data is perfectly valid.
    }

    return address;
  }

  Geo createGeoFromMap(Map<String, String> map) throws GeoDataParsingException {
    Objects.requireNonNull(map);

    final Geo geo;

    try {
      double lat = Double.parseDouble(map.get("latitude"));
      double lon = Double.parseDouble(map.get("longitude"));
      double searchRadius = Double.parseDouble(map.get("searchRadius"));
      geo = new Geo(lat, lon, searchRadius);
    }
    catch (NumberFormatException e) {
      throw new GeoDataParsingException();
    }

    return geo;
  }

  private GeocodingService getGeocodingService() {
    return getGeocodingWorker().getGeocodingService();
  }

  private GeocodingWorker getGeocodingWorker() {
    return (GeocodingWorker) workerService.getWorker();
  }

  static class GeoDataParsingException extends Exception {
  }
}
