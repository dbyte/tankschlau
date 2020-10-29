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

package de.fornalik.tankschlau.webserviceapi.common;

import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStations;
import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.util.Localization;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

public class PetrolStationMessageWorker {
  private static final Localization L10N = Localization.getInstance();
  private static final Logger LOGGER = Logger.getLogger(PetrolStationMessageWorker.class.getName());

  private final MessageService messageService;
  private final PetrolStationMessageContent messageContent;

  public PetrolStationMessageWorker(
      MessageService messageService,
      PetrolStationMessageContent messageContent) {

    this.messageService = Objects.requireNonNull(messageService);
    this.messageContent = Objects.requireNonNull(messageContent);
  }

  public void checkSendMessage(List<PetrolStation> stations, PetrolType petrolType) {
    if (stations == null || stations.size() == 0) {
      return;
    }

    PetrolStations.sortByPriceAndDistanceForPetrolType(stations, petrolType);
    PetrolStation cheapestStation = stations.get(0);

    messageContent.reset();
    messageContent.setMessage(cheapestStation, petrolType);

    messageService.sendMessage(messageContent);

    Optional<String> responseErrorMsg = messageService.getTransactInfo().getErrorMessage();

    responseErrorMsg.ifPresent(
        errMsg -> LOGGER.warning(L10N.get("msg.SendPushMessageFailed", errMsg)));
  }
}
