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
import de.fornalik.tankschlau.service.*;
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.station.PetrolsJsonAdapter;
import de.fornalik.tankschlau.user.ApiKeyManager;
import de.fornalik.tankschlau.user.ApiKeyStore;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.user.UserPrefsApiKeyStore;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.LoggingConfig;
import de.fornalik.tankschlau.webserviceapi.common.MessageRequest;
import de.fornalik.tankschlau.webserviceapi.common.MessageService;
import de.fornalik.tankschlau.webserviceapi.common.PetrolStationMessageWorker;
import de.fornalik.tankschlau.webserviceapi.common.PetrolStationsWebService;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingClient;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingRequest;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingResponse;
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
 * Describes the dependency graph throughout the application, pre-building objects.
 * Avoid tight coupling using Inversion Of Control.
 * Note: ONLY call it's members from the root of the app, mostly this will be the "main" method.
 */
final class TankSchlauBootstrap {
  final UserPrefs userPrefs;
  final ApiKeyStore apiKeyStore;
  final ApiKeyManager apiKeyManager;
  final ApiKeyManager geocodingApikeyManager;
  final ApiKeyManager tankerkoenigApikeyManager;
  final GeocodingWorker geocodingWorker;
  final PetrolStationsWorker petrolStationsWorker;
  final PetrolStationMessageWorker petrolStationMessageWorker;

  TankSchlauBootstrap() {
    /*
    Setup application context dependency graph
    */

    // =====================================================================
    // region Common

    LoggingConfig.init();

    Localization l10n = Localization.getInstance();
    l10n.configure(Locale.GERMANY);

    userPrefs = new UserPrefs("/de/fornalik/tankschlau");
    apiKeyStore = new UserPrefsApiKeyStore(userPrefs);
    apiKeyManager = ApiKeyManager.createForPushMessage(apiKeyStore);
    geocodingApikeyManager = ApiKeyManager.createForGeocoding(apiKeyStore);
    tankerkoenigApikeyManager = ApiKeyManager.createForPetrolStations(apiKeyStore);

    HttpClient httpClient = new OkHttpClient(new okhttp3.OkHttpClient());

    Gson jsonProvider = new GsonBuilder()
        .registerTypeAdapter(Petrols.class, new PetrolsJsonAdapter())
        .create();

    // endregion
    // =====================================================================

    // region Geocoding Webservice

    JsonResponse geocodingResponse = new GoogleGeocodingResponse(
        jsonProvider,
        new ResponseBodyImpl(),
        new TransactInfoImpl());

    GeocodingService geocodingService = new GoogleGeocodingClient(
        httpClient,
        GoogleGeocodingRequest.create(geocodingApikeyManager),
        geocodingResponse);

    geocodingWorker = new GeocodingWorker(geocodingService);

    // endregion
    // =====================================================================

    // region PetrolStations Webservice

    TankerkoenigJsonAdapter tankerkoenigJsonAdapter = new TankerkoenigJsonAdapter(jsonProvider);

    JsonResponse petrolStationsJsonResponse = new TankerkoenigResponse(
        jsonProvider,
        new ResponseBodyImpl(),
        new TransactInfoImpl());

    PetrolStationsRepo petrolStationsRepo = new TankerkoenigPetrolStationsRepo(
        httpClient,
        tankerkoenigJsonAdapter,
        TankerkoenigRequest.create(tankerkoenigApikeyManager),
        petrolStationsJsonResponse);

    PetrolStationsService petrolStationsService = new PetrolStationsWebService(petrolStationsRepo);

    petrolStationsWorker = new PetrolStationsWorker(petrolStationsService);

    // endregion
    // =====================================================================

    // region PetrolStations Push Message Webservice

    MessageRequest messageRequest = new PushoverMessageRequest(apiKeyManager, userPrefs);
    JsonResponse messageResponse = new PushoverMessageResponse(
        jsonProvider,
        new ResponseBodyImpl(),
        new TransactInfoImpl());

    MessageService messageService = new PushoverMessageService(
        httpClient,
        messageRequest,
        messageResponse);

    petrolStationMessageWorker = new PetrolStationMessageWorker(
        messageService,
        new PushoverMessageContent(),
        userPrefs);

    // endregion
    // =====================================================================
  }
}
