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

package de.fornalik.tankschlau.bootstrap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.OkHttpClient;
import de.fornalik.tankschlau.station.PetrolStationsDao;
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.station.PetrolsJsonAdapter;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.user.UserPrefsApiKeyStore;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.common.GeoRequest;
import de.fornalik.tankschlau.webserviceapi.common.GeocodingClient;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingClient;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingRequest;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigJsonAdapter;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigPetrolStationsDao;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigRequest;

import java.util.Locale;

/**
 * Describes a the dependency graph throughout the application.
 * Avoid tight coupling to any classes by ONLY calling it's static members from the root of the app.
 */
public class AppContainer {
  // Configuration
  public static final Localization L10N = new Localization(Locale.GERMAN);
  public static final UserPrefs USER_PREFS = new UserPrefs("/de/fornalik/tankschlau");
  public static final HttpClient HTTP_CLIENT = new OkHttpClient();

  public static final Gson JSON_PROVIDER = new GsonBuilder()
      .registerTypeAdapter(Petrols.class, new PetrolsJsonAdapter())
      .create();

  public static final TankerkoenigJsonAdapter PETROL_STATIONS_JSON_ADAPTER =
      new TankerkoenigJsonAdapter(JSON_PROVIDER);

  public static final ApiKeyStore API_KEY_STORE = new UserPrefsApiKeyStore(USER_PREFS);

  public static final ApiKeyManager PUSHMESSAGE_APIKEY_MANAGER =
      ApiKeyManager.createForPushMessage(API_KEY_STORE);
  public static final ApiKeyManager GEOCODING_APIKEY_MANAGER =
      ApiKeyManager.createForGeocoding(API_KEY_STORE);
  public static final ApiKeyManager TANKERKOENIG_APIKEY_MANAGER =
      ApiKeyManager.createForPetrolStations(API_KEY_STORE);

  public static final GeocodingClient GEOCODING_CLIENT = new GoogleGeocodingClient(
      HTTP_CLIENT,
      JSON_PROVIDER,
      GoogleGeocodingRequest.create(GEOCODING_APIKEY_MANAGER));

  public static final GeoRequest GEO_REQUEST = TankerkoenigRequest.create(
      TANKERKOENIG_APIKEY_MANAGER);

  public static final PetrolStationsDao PETROL_STATIONS_DAO = new TankerkoenigPetrolStationsDao(
      HTTP_CLIENT,
      PETROL_STATIONS_JSON_ADAPTER,
      JSON_PROVIDER,
      GEO_REQUEST);
}
