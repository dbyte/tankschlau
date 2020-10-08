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

package de.fornalik.tankschlau;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.gui.window.MainWindow;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.OkHttpClient;
import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.station.PetrolsJsonAdapter;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.user.UserPrefsApiKeyStore;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.common.GeocodingClient;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingClient;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigJsonAdapter;

import javax.swing.*;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

public class TankSchlau {
  // Configuration
  public static final Localization L10N = new Localization(Locale.GERMAN);
  public static final UserPrefs USER_PREFS = new UserPrefs();
  public static final HttpClient HTTP_CLIENT = new OkHttpClient();

  public static final Gson JSON_PROVIDER = new GsonBuilder()
      .registerTypeAdapter(Petrols.class, new PetrolsJsonAdapter())
      .create();

  public static final TankerkoenigJsonAdapter PETROL_STATIONS_JSON_ADAPTER =
      new TankerkoenigJsonAdapter(JSON_PROVIDER);

  private static final ApiKeyStore API_KEY_STORE = new UserPrefsApiKeyStore(USER_PREFS);

  public static final ApiKeyManager TANKERKOENIG_APIKEY_MANAGER =
      ApiKeyManager.createForPetrolStations(API_KEY_STORE);
  public static final ApiKeyManager GEOCODING_APIKEY_MANAGER =
      ApiKeyManager.createForGeocoding(API_KEY_STORE);
  public static final ApiKeyManager PUSHMESSAGE_APIKEY_MANAGER =
      ApiKeyManager.createForPushMessage(API_KEY_STORE);

  public static void main(String[] args) {
    TankSchlau instance = new TankSchlau();
    instance.processVmOptions();

    // Ex: Writing some user geo data to user prefs
    // instance.processTestAddress();

    Geo userGeo = USER_PREFS
        .readGeo()
        .orElseThrow(() -> new IllegalStateException("No preferences found for user geo data."));

    SwingUtilities.invokeLater(
        () -> {
          MainWindow mainWindow = new MainWindow();
          mainWindow.initGui();
          mainWindow.updateList(userGeo, PetrolType.DIESEL);
        }
    );
  }

  private void processVmOptions() {
    // Offer option to pass some data at startup. Ex: -Dmyproperty="My value"

    Optional.ofNullable(System.getProperty("petrolStationsApiKey"))
            .ifPresent(TANKERKOENIG_APIKEY_MANAGER::write);

    Optional.ofNullable(System.getProperty("geocodingApiKey"))
            .ifPresent(GEOCODING_APIKEY_MANAGER::write);

    Optional.ofNullable(System.getProperty("pushmessageApiKey"))
            .ifPresent(PUSHMESSAGE_APIKEY_MANAGER::write);

    Optional.ofNullable(System.getProperty("pushmessageUserId"))
            .ifPresent(USER_PREFS::writePushMessageUserId);
  }

  // Example: Writing some user address and geo data to user prefs.
  private void processTestAddress() {
    Address address = new Address("An den Äckern", "Wolfsburg", "38446");
    GeocodingClient geocodingClient = new GoogleGeocodingClient();

    try {
      geocodingClient.getGeo(address).ifPresent(address::setGeo);
      address.getGeo().ifPresent(g -> g.setDistance(10.0));
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    USER_PREFS.writeAddress(address);
  }
}