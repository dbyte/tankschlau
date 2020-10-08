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
import de.fornalik.tankschlau.station.PetrolStations;
import de.fornalik.tankschlau.station.PetrolStationsDao;
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.station.PetrolsJsonAdapter;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.user.UserPrefsApiKeyStore;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.webserviceapi.common.*;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingClient;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingRequest;
import de.fornalik.tankschlau.webserviceapi.pushover.PushoverMessageClient;
import de.fornalik.tankschlau.webserviceapi.pushover.PushoverMessageContent;
import de.fornalik.tankschlau.webserviceapi.pushover.PushoverMessageRequest;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigJsonAdapter;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigPetrolStationsDao;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigRequest;

import java.util.Locale;

/**
 * Describes a the dependency graph throughout the application.
 * Avoid tight coupling to any classes by ONLY calling it's members from the root of the app.
 */
public final class AppContainer {
  public final Localization L10N;
  public final UserPrefs USER_PREFS;
  public final HttpClient HTTP_CLIENT;
  public final Gson JSON_PROVIDER;
  public final TankerkoenigJsonAdapter PETROL_STATIONS_JSON_ADAPTER;
  public final ApiKeyStore API_KEY_STORE;
  public final ApiKeyManager PUSHMESSAGE_APIKEY_MANAGER;
  public final ApiKeyManager GEOCODING_APIKEY_MANAGER;
  public final ApiKeyManager TANKERKOENIG_APIKEY_MANAGER;
  public final GeocodingClient GEOCODING_CLIENT;
  public final GeoRequest GEO_REQUEST;
  public final PetrolStationsDao PETROL_STATIONS_DAO;
  public final PetrolStations PETROL_STATIONS_SERVICE;
  public final PetrolStationMessageContent PETROL_STATION_MESSAGE_CONTENT;
  public final MessageRequest MESSAGE_REQUEST;
  public final MessageClient MESSAGE_CLIENT;

  public AppContainer() {
    // Setup dependency graph
    L10N = new Localization(Locale.GERMAN);
    USER_PREFS = new UserPrefs("/de/fornalik/tankschlau");
    HTTP_CLIENT = new OkHttpClient();

    JSON_PROVIDER = new GsonBuilder()
        .registerTypeAdapter(Petrols.class, new PetrolsJsonAdapter())
        .create();

    API_KEY_STORE = new UserPrefsApiKeyStore(USER_PREFS);
    PUSHMESSAGE_APIKEY_MANAGER = ApiKeyManager.createForPushMessage(API_KEY_STORE);
    GEOCODING_APIKEY_MANAGER = ApiKeyManager.createForGeocoding(API_KEY_STORE);
    TANKERKOENIG_APIKEY_MANAGER = ApiKeyManager.createForPetrolStations(API_KEY_STORE);

    GEOCODING_CLIENT = new GoogleGeocodingClient(
        HTTP_CLIENT,
        JSON_PROVIDER,
        GoogleGeocodingRequest.create(GEOCODING_APIKEY_MANAGER));

    GEO_REQUEST = TankerkoenigRequest.create(TANKERKOENIG_APIKEY_MANAGER);
    PETROL_STATIONS_JSON_ADAPTER = new TankerkoenigJsonAdapter(JSON_PROVIDER);

    PETROL_STATIONS_DAO = new TankerkoenigPetrolStationsDao(
        HTTP_CLIENT,
        PETROL_STATIONS_JSON_ADAPTER,
        JSON_PROVIDER,
        GEO_REQUEST);

    PETROL_STATIONS_SERVICE = new PetrolStations(PETROL_STATIONS_DAO);

    PETROL_STATION_MESSAGE_CONTENT = new PushoverMessageContent(L10N);
    MESSAGE_REQUEST = new PushoverMessageRequest(PUSHMESSAGE_APIKEY_MANAGER, USER_PREFS);
    MESSAGE_CLIENT = new PushoverMessageClient(HTTP_CLIENT, MESSAGE_REQUEST);
  }
}
