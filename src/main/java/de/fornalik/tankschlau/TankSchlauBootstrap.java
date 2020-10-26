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
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.JsonResponse;
import de.fornalik.tankschlau.net.OkHttpClient;
import de.fornalik.tankschlau.net.ResponseBodyImpl;
import de.fornalik.tankschlau.station.PetrolStations;
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.station.PetrolsJsonAdapter;
import de.fornalik.tankschlau.storage.PetrolStationsRepo;
import de.fornalik.tankschlau.storage.PetrolStationsService;
import de.fornalik.tankschlau.storage.PetrolStationsWorker;
import de.fornalik.tankschlau.storage.TransactInfoImpl;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.user.UserPrefsApiKeyStore;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.LoggingConfig;
import de.fornalik.tankschlau.webserviceapi.common.*;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingClient;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingRequest;
import de.fornalik.tankschlau.webserviceapi.pushover.PushoverMessageContent;
import de.fornalik.tankschlau.webserviceapi.pushover.PushoverMessageRequest;
import de.fornalik.tankschlau.webserviceapi.pushover.PushoverMessageResponse;
import de.fornalik.tankschlau.webserviceapi.pushover.PushoverMessageService;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigJsonAdapter;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigPetrolStationsRepo;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigRequest;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigResponse;

import java.util.Locale;

/**
 * Describes a the dependency graph throughout the application.
 * Avoid tight coupling to any classes by ONLY calling it's members from the root of the app.
 */
final class TankSchlauBootstrap {
  final UserPrefs userPrefs;
  final HttpClient httpClient;
  final Gson jsonProvider;
  final TankerkoenigJsonAdapter tankerkoenigJsonAdapter;
  final ApiKeyStore apiKeyStore;
  final ApiKeyManager apiKeyManager;
  final ApiKeyManager geocodingApikeyManager;
  final ApiKeyManager tankerkoenigApikeyManager;
  final GeocodingService geocodingService;
  final GeoRequest geoRequest;
  final PetrolStationsRepo petrolStationsRepo;
  final JsonResponse petrolStationsJsonResponse;
  final PetrolStationsService petrolStationsService;
  final PetrolStations petrolStations;
  final PetrolStationsWorker petrolStationsWorker;
  final PetrolStationMessageContent petrolStationMessageContent;
  final MessageRequest messageRequest;
  final JsonResponse messageResponse;
  final MessageService messageClient;

  TankSchlauBootstrap() {
    /*
    Setup application context dependency graph
    */

    LoggingConfig.init();

    Localization l10n = Localization.getInstance();
    l10n.configure(Locale.GERMANY);

    userPrefs = new UserPrefs("/de/fornalik/tankschlau");
    httpClient = new OkHttpClient(new okhttp3.OkHttpClient());

    jsonProvider = new GsonBuilder()
        .registerTypeAdapter(Petrols.class, new PetrolsJsonAdapter())
        .create();

    apiKeyStore = new UserPrefsApiKeyStore(userPrefs);
    apiKeyManager = ApiKeyManager.createForPushMessage(apiKeyStore);
    geocodingApikeyManager = ApiKeyManager.createForGeocoding(apiKeyStore);
    tankerkoenigApikeyManager = ApiKeyManager.createForPetrolStations(apiKeyStore);

    petrolStationsJsonResponse = new TankerkoenigResponse(
        jsonProvider,
        new ResponseBodyImpl(),
        new TransactInfoImpl());

    geocodingService = new GoogleGeocodingClient(
        httpClient,
        GoogleGeocodingRequest.create(geocodingApikeyManager),
        petrolStationsJsonResponse);

    geoRequest = TankerkoenigRequest.create(tankerkoenigApikeyManager);
    tankerkoenigJsonAdapter = new TankerkoenigJsonAdapter(jsonProvider);

    petrolStationsRepo = new TankerkoenigPetrolStationsRepo(
        httpClient,
        tankerkoenigJsonAdapter,
        geoRequest,
        petrolStationsJsonResponse);

    petrolStationsService = new PetrolStationsWebService(petrolStationsRepo);

    petrolStations = new PetrolStations(petrolStationsService);

    petrolStationsWorker = new PetrolStationsWorker(petrolStationsService);

    petrolStationMessageContent = new PushoverMessageContent();
    messageRequest = new PushoverMessageRequest(apiKeyManager, userPrefs);
    messageResponse = new PushoverMessageResponse(
        jsonProvider,
        new ResponseBodyImpl(),
        new TransactInfoImpl());
    messageClient = new PushoverMessageService(httpClient, messageRequest, messageResponse);
  }
}
