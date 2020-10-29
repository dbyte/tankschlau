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
import de.fornalik.tankschlau.station.Petrols;
import de.fornalik.tankschlau.util.Localization;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.logging.Logger;

// TODO unit tests

/**
 * Worker for push messaging, dedicated to the current cheapest petrol price for a given petrol
 * type in a list of petrol stations.
 */
public class PetrolStationMessageWorker {
  private static final Localization L10N = Localization.getInstance();
  private static final Logger LOGGER = Logger.getLogger(PetrolStationMessageWorker.class.getName());

  private static int callsSinceLastMessage = 0;
  private static double priceAtLastSentMessage = 0.0;

  private final MessageService messageService;
  private final PetrolStationMessageContent messageContent;
  private int maxRequestsUntilForceSendIfPriceIsRisingSinceLastMsg;

  public PetrolStationMessageWorker(
      MessageService messageService,
      PetrolStationMessageContent messageContent) {

    this.messageService = Objects.requireNonNull(messageService);
    this.messageContent = Objects.requireNonNull(messageContent);
    this.maxRequestsUntilForceSendIfPriceIsRisingSinceLastMsg = 2;
  }

  /**
   * @param stations            List of petrol station in which to find the station of interest.
   * @param preferredPetrolType Type of petrol the message receiver is interested in.
   * @see PetrolStations#sortByPriceAndDistanceForPetrolType(List, PetrolType)
   */
  public synchronized void checkSendMessage(
      List<PetrolStation> stations,
      PetrolType preferredPetrolType) {

    if (stations == null || stations.size() == 0)
      return;

    callsSinceLastMessage++;

    // Find cheapest station according to our business rules.
    Optional<PetrolStation> cheapestStation = PetrolStations
        .findCheapest(stations, preferredPetrolType);

    if (!cheapestStation.isPresent())
      return;

    // Get price
    double price = Petrols
        .findPetrol(cheapestStation.get().getPetrols(), preferredPetrolType)
        .map(petrol -> petrol.price).orElse(0.0);

    if (!mustSend(price))
      return;

    // Form the message content.
    messageContent.reset();
    messageContent.setMessage(cheapestStation.get(), preferredPetrolType);

    // Send message.
    messageService.sendMessage(messageContent);
    callsSinceLastMessage = 0;

    Optional<String> responseErrorMsg = messageService.getTransactInfo().getErrorMessage();

    responseErrorMsg.ifPresent(
        errMsg -> LOGGER.warning(L10N.get("msg.SendPushMessageFailed", errMsg)));
  }

  private boolean mustSend(double price) {
    boolean isPriceReduction = price > 0.0 && price < priceAtLastSentMessage;
    boolean exceededMaxCallsUntilForceSend =
        callsSinceLastMessage >= maxRequestsUntilForceSendIfPriceIsRisingSinceLastMsg;

    return isPriceReduction || exceededMaxCallsUntilForceSend;
  }
}
