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
import de.fornalik.tankschlau.webserviceapi.common.*;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Locale;

/**
 * Composition Root. Describes the dependency graph throughout the application,
 * pre-building objects. Avoids tight coupling using Inversion Of Control.
 * Note: ONLY call it's members from the root of the app, mostly this will be the "main" method.
 */
@Configuration
class TankSchlauContext {

  TankSchlauContext() {
    LoggingConfig.init();

    Localization l10n = Localization.getInstance();
    l10n.configure(Locale.GERMANY);
  }

  @Bean
  UserPrefs userPrefs() {
    return new UserPrefs("/de/fornalik/tankschlau");
  }

  @Bean
  ApiKeyStore userPrefsApiKeyStore() {
    return new UserPrefsApiKeyStore(userPrefs());
  }

  @Bean
  ApiKeyManager tankerkoenigApikeyManager() {
    return ApiKeyManager.createForPetrolStations(userPrefsApiKeyStore());
  }

  @Bean
  ApiKeyManager geocodingApikeyManager() {
    return ApiKeyManager.createForGeocoding(userPrefsApiKeyStore());
  }

  @Bean
  ApiKeyManager pushMessageApikeyManager() {
    return ApiKeyManager.createForPushMessage(userPrefsApiKeyStore());
  }

  @Bean
  HttpClient okHttpClient() {
    return new OkHttpClient(new okhttp3.OkHttpClient());
  }

  @Bean
  Gson jsonProvider() {
    return new GsonBuilder()
        .registerTypeAdapter(Petrols.class, new PetrolsJsonAdapter())
        .create();
  }

  @Bean
  PetrolStationsWorker petrolStationsWorker() {
    return new PetrolStationsWorker(petrolStationsService());
  }

  @Bean
  PetrolStationMessageWorker petrolStationMessageWorker() {
    return new PetrolStationMessageWorker(
        pushoverMessageService(),
        new PushoverMessageContent(),
        userPrefs());
  }

  @Bean
  PetrolStationsService petrolStationsService() {
    return new PetrolStationsWebService(
        petrolStationsRepo());
  }

  @Bean
  PetrolStationsRepo petrolStationsRepo() {
    return new TankerkoenigPetrolStationsRepo(
        okHttpClient(),
        new TankerkoenigJsonAdapter(jsonProvider()),
        TankerkoenigRequest.create(tankerkoenigApikeyManager()),
        petrolStationsResponse());
  }

  @Bean
  JsonResponse petrolStationsResponse() {
    return new TankerkoenigResponse(
        jsonProvider(),
        new ResponseBodyImpl(),
        new TransactInfoImpl());
  }

  @Bean
  GeocodingWorker geocodingWorker() {
    return new GeocodingWorker(geocodingService());
  }

  @Bean
  GeocodingService geocodingService() {
    return new GoogleGeocodingClient(
        okHttpClient(),
        geocodingRequest(),
        geocodingResponse());
  }

  @Bean
  AddressRequest geocodingRequest() {
    return GoogleGeocodingRequest.create(geocodingApikeyManager());
  }

  @Bean
  JsonResponse geocodingResponse() {
    return new GoogleGeocodingResponse(
        jsonProvider(),
        new ResponseBodyImpl(),
        new TransactInfoImpl());
  }

  @Bean
  MessageService pushoverMessageService() {
    return new PushoverMessageService(
        okHttpClient(),
        pushoverMessageRequest(),
        pushoverMessageResponse());
  }

  @Bean
  MessageRequest pushoverMessageRequest() {
    return new PushoverMessageRequest(
        pushMessageApikeyManager(),
        userPrefs());
  }

  @Bean
  JsonResponse pushoverMessageResponse() {
    return new PushoverMessageResponse(
        jsonProvider(),
        new ResponseBodyImpl(),
        new TransactInfoImpl());
  }
}
