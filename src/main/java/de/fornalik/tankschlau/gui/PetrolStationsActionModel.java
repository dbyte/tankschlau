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

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.service.PetrolStationsWorker;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.WorkerService;
import de.fornalik.tankschlau.webserviceapi.common.PetrolStationMessageWorker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

@Component
class PetrolStationsActionModel {

  private static final Logger LOGGER = Logger.getLogger(PetrolStationsActionModel.class.getName());
  private static final Localization L10N = Localization.getInstance();

  private final WorkerService<List<PetrolStation>> petrolStationsWorkerService;
  private final PetrolStationMessageWorker messageWorker;
  private final UserPrefs userPrefs;

  @Autowired
  PetrolStationsActionModel(
      WorkerService<List<PetrolStation>> petrolStationsWorkerService,
      PetrolStationMessageWorker messageWorker,
      UserPrefs userPrefs) {

    this.petrolStationsWorkerService = petrolStationsWorkerService;
    this.messageWorker = messageWorker;
    this.userPrefs = userPrefs;
  }

  @PostConstruct
  private void init() {
    petrolStationsWorkerService.setTimeUnit(TimeUnit.SECONDS);
  }

  void updatePetrolStations(Consumer<List<PetrolStation>> callback) {
    getPetrolStationsWorker().setUserGeo(readUserGeoFromPrefs());
    petrolStationsWorkerService.startOneShot(callback);
  }

  void startCyclicUpdatePetrolStations(Consumer<List<PetrolStation>> callback) {
    getPetrolStationsWorker().setUserGeo(readUserGeoFromPrefs());
    petrolStationsWorkerService.startCyclic(callback, readCycleRateFromPrefs());
  }

  void startObservingCycleCountdown(BiConsumer<Long, TimeUnit> callback) {
    TimeUnit timeUnit = petrolStationsWorkerService.getTimeUnit();
    petrolStationsWorkerService.processCountdown(remaining -> callback.accept(remaining, timeUnit));
  }

  void stopCyclicUpdatePetrolStations() {
    petrolStationsWorkerService.stopCyclic();
  }

  void sendPushmessage(List<PetrolStation> petrolStations) {
    messageWorker.execute(petrolStations, userPrefs.readPreferredPetrolType());
  }

  private PetrolStationsWorker getPetrolStationsWorker() {
    return ((PetrolStationsWorker) petrolStationsWorkerService.getWorker());
  }

  private int readCycleRateFromPrefs() {
    return userPrefs.readPetrolStationsUpdateCycleRate();
  }

  private Geo readUserGeoFromPrefs() {
    Optional<Geo> geo = userPrefs.readGeo();

    if (!geo.isPresent()) {
      String errMessage = L10N.get("msg.UnableToRequestPetrolStations_ReasonNoGeoForUser");
      LOGGER.warning(errMessage);
      throw new IllegalStateException(errMessage);
    }

    return geo.get();
  }
}
