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

package de.fornalik.tankschlau.service;

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.RunnableCallbackWorker;

import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

// TODO unit tests

/**
 * Worker for PetrolStationsService.
 */
public class PetrolStationsWorker implements RunnableCallbackWorker<List<PetrolStation>> {
  private static final Logger LOGGER = Logger.getLogger(PetrolStationsWorker.class.getName());
  private static final Localization L10N = Localization.getInstance();

  private final PetrolStationsService petrolStationsService;
  private Geo userGeo;
  private Consumer<List<PetrolStation>> callback;

  public PetrolStationsWorker(PetrolStationsService petrolStationsService) {
    this.petrolStationsService = Objects.requireNonNull(petrolStationsService);
    this.userGeo = null;
    this.callback = null;
  }

  public void setUserGeo(Geo userGeo) {
    this.userGeo = Objects.requireNonNull(userGeo);
  }

  @Override
  public void setCallback(Consumer<List<PetrolStation>> callback) {
    this.callback = callback;
  }

  @Override
  public void run() {
    LOGGER.info(L10N.get("msg.PriceRequestRunning"));
    List<PetrolStation> data = new ArrayList<>();

    try {
      data = findPetrolStations();
    }

    catch (Exception e) {
      String errMsg = e.getMessage()
          + ". Trace: \n" + Arrays.toString(e.getStackTrace()).replace(", ", "\n")
          + "\n";

      LOGGER.severe(errMsg);

      // Interrupting my own thread does NOT invoke an InterruptedException and is always permitted.
      Thread.currentThread().interrupt();
    }

    finally {
      LOGGER.info(L10N.get("msg.PriceRequestDone"));
      callback.accept(data);
    }
  }

  private List<PetrolStation> findPetrolStations() {
    List<PetrolStation> data = petrolStationsService.getNeighbourhoodStations((userGeo));
    Optional<String> errorMessage = petrolStationsService.getTransactInfo().getErrorMessage();

    errorMessage.ifPresent(
        message -> LOGGER.warning(L10N.get("msg.ErrorServerConnection", message)));

    if (data.isEmpty()) {
      LOGGER.warning(L10N.get("msg.NoPetrolStationsFoundInNeighbourhood"));
    }

    return data;
  }
}
