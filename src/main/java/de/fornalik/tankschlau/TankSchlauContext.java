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
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.gui.SwingWorkerService;
import de.fornalik.tankschlau.net.HttpClient;
import de.fornalik.tankschlau.net.JsonResponse;
import de.fornalik.tankschlau.net.OkHttpClient;
import de.fornalik.tankschlau.net.ResponseBodyImpl;
import de.fornalik.tankschlau.service.GeocodingService;
import de.fornalik.tankschlau.service.GeocodingWorker;
import de.fornalik.tankschlau.service.PetrolStationsRepo;
import de.fornalik.tankschlau.service.PetrolStationsService;
import de.fornalik.tankschlau.service.PetrolStationsWorker;
import de.fornalik.tankschlau.service.TransactInfoImpl;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.station.PetrolsJsonAdapter;
import de.fornalik.tankschlau.user.ApiKeyManager;
import de.fornalik.tankschlau.user.ApiKeyStore;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.user.UserPrefsApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.common.AddressRequest;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Composition Root. Describes the dependency graph throughout the application,
 * pre-building objects. Avoids tight coupling using Inversion Of Control.
 * Note: ONLY call it's members from the root of the app, mostly this will be the "main" method.
 */
@Configuration
class TankSchlauContext {

  @Bean
  UserPrefs userPrefs() {
    return new UserPrefs("/de/fornalik/tankschlau");
  }

  @Bean
  ApiKeyStore userPrefsApiKeyStore() {
    return new UserPrefsApiKeyStore(userPrefs());
  }

  @Bean
  ApiKeyManager apiKeyManagerPetrolStations() {
    return ApiKeyManager.createForPetrolStations(userPrefsApiKeyStore());
  }

  @Bean
  ApiKeyManager apiKeyManagerGeocoding() {
    return ApiKeyManager.createForGeocoding(userPrefsApiKeyStore());
  }

  @Bean
  ApiKeyManager apiKeyManagerPushMessage() {
    return ApiKeyManager.createForPushMessage(userPrefsApiKeyStore());
  }

  @Bean
  HttpClient httpClient() {
    return new OkHttpClient(new okhttp3.OkHttpClient());
  }

  @Bean
  Gson jsonProvider() {
    return new GsonBuilder()
        .registerTypeAdapter(Petrols.class, new PetrolsJsonAdapter())
        .create();
  }

  @Bean
  SwingWorkerService<List<PetrolStation>> petrolStationsWorkerService() {
    return new SwingWorkerService<>(petrolStationsWorker());
  }

  @Bean
  PetrolStationsWorker petrolStationsWorker() {
    return new PetrolStationsWorker(petrolStationsService());
  }

  @Bean
  PetrolStationsService petrolStationsService() {
    return new PetrolStationsWebService(
        petrolStationsRepo());
  }

  @Bean
  PetrolStationsRepo petrolStationsRepo() {
    return new TankerkoenigPetrolStationsRepo(
        httpClient(),
        new TankerkoenigJsonAdapter(jsonProvider()),
        TankerkoenigRequest.create(apiKeyManagerPetrolStations()),
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
  PetrolStationMessageWorker petrolStationMessageWorker() {
    return new PetrolStationMessageWorker(
        pushoverMessageService(),
        new PushoverMessageContent(),
        userPrefs());
  }

  @Bean
  MessageService pushoverMessageService() {
    return new PushoverMessageService(
        httpClient(),
        pushoverMessageRequest(),
        pushoverMessageResponse());
  }

  @Bean
  MessageRequest pushoverMessageRequest() {
    return new PushoverMessageRequest(
        apiKeyManagerPushMessage(),
        userPrefs());
  }

  @Bean
  JsonResponse pushoverMessageResponse() {
    return new PushoverMessageResponse(
        jsonProvider(),
        new ResponseBodyImpl(),
        new TransactInfoImpl());
  }

  @Bean
  SwingWorkerService<Geo> geocodingWorkerService() {
    return new SwingWorkerService<>(geocodingWorker());
  }

  @Bean
  GeocodingWorker geocodingWorker() {
    return new GeocodingWorker(geocodingService());
  }

  @Bean
  GeocodingService geocodingService() {
    return new GoogleGeocodingClient(
        httpClient(),
        geocodingRequest(),
        geocodingResponse());
  }

  @Bean
  AddressRequest geocodingRequest() {
    return GoogleGeocodingRequest.create(apiKeyManagerGeocoding());
  }

  @Bean
  JsonResponse geocodingResponse() {
    return new GoogleGeocodingResponse(
        jsonProvider(),
        new ResponseBodyImpl(),
        new TransactInfoImpl());
  }
}
