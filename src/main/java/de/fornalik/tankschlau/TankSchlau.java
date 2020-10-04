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

import com.google.gson.TypeAdapter;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.gui.window.MainWindow;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.OkHttpClient;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStationsJsonAdapter;
import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.UserPrefs;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.common.UserPrefsApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.google.GeocodingApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigApiKeyManager;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Locale;

public class TankSchlau {
  // Configuration
  public static final Localization L10N = new Localization(Locale.GERMAN);
  public static final UserPrefs USER_PREFS = new UserPrefs();
  public static final HttpClient HTTP_CLIENT = new OkHttpClient();
  public static final TypeAdapter<List<PetrolStation>> PETROL_STATIONS_JSON_ADAPTER =
      new PetrolStationsJsonAdapter();

  private static final ApiKeyStore API_KEY_STORE = new UserPrefsApiKeyStore(USER_PREFS);

  public static final ApiKeyManager TANKERKOENIG_APIKEY_MANAGER =
      new TankerkoenigApiKeyManager(API_KEY_STORE);

  public static final ApiKeyManager GEOCODING_APIKEY_MANAGER =
      new GeocodingApiKeyManager(API_KEY_STORE);

  // Start application
  public static void main(String[] args) throws MalformedURLException {
    // Offer option to pass a tankerkoenig- and geocoding-api-key at startup which we persist
    // in a specified storage.
    if (args.length >= 1)
      TANKERKOENIG_APIKEY_MANAGER.write(args[0]);
    if (args.length >= 2)
      GEOCODING_APIKEY_MANAGER.write(args[1]);

    // Example: Writing some user geo data to user prefs
    // USER_PREFS.writeGeo(new Geo(52.4079755, 10.7725368, 8.0));

    Geo userGeo = USER_PREFS.readGeo().orElseThrow(
        () -> new IllegalStateException("No preferences found for user geo data."));

    MainWindow mainWindow = new MainWindow();

    mainWindow.initGui();
    mainWindow.updateList(userGeo, PetrolType.DIESEL);
  }
}