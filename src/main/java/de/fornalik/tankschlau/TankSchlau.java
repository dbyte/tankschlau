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
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStationsJsonAdapter;
import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.UserPrefs;
import de.fornalik.tankschlau.webserviceapi.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.ApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.UserPrefsApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigRequest;

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

  // Start application
  public static void main(String[] args) throws MalformedURLException {
    // Offer option to pass a tankerkoenig.de API key at startup which then gets persisted
    // in a specified storage.
    if (args.length >= 1)
      TANKERKOENIG_APIKEY_MANAGER.write(args[0]);

    // Write some user geo data to user prefs.
    USER_PREFS.writeGeo(new Geo(48.0348466, 11.9068076, 10.0));

    // Default Geo if no prefs exist.
    final Geo[] geo = {new Geo(52.408306, 10.77200, 5.0)};
    USER_PREFS.readGeo().ifPresent(g -> geo[0] = g);

    MainWindow mainWindow = new MainWindow();
    Request request = TankerkoenigRequest.create(TANKERKOENIG_APIKEY_MANAGER, geo[0]);

    mainWindow.initGui();
    mainWindow.updateList(request, PetrolType.DIESEL);
  }
}