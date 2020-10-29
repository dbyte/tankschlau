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
  private final int maxRequestsUntilForceSendIfPriceIsRisingSinceLastMsg;

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

    if (stations == null || stations.size() == 0) {
      LOGGER.fine("Given stations are null or empty, so no push message to send.");
      return;
    }

    callsSinceLastMessage++;
    LOGGER.finer("Valid calls since last message: " + callsSinceLastMessage);

    PetrolStation cheapestStation = findCheapestStation(stations, preferredPetrolType);
    double price = findPrice(cheapestStation, preferredPetrolType);

    if (!mustSend(price))
      return;

    doSendMessage(cheapestStation, preferredPetrolType, price);
  }

  private void doSendMessage(
      PetrolStation cheapestStation,
      PetrolType preferredPetrolType,
      double currentPrice) {

    // Form the message content.
    messageContent.reset();
    messageContent.setMessage(cheapestStation, preferredPetrolType);

    // Send message.
    LOGGER.finer("Invoking message service.");
    messageService.sendMessage(messageContent);
    callsSinceLastMessage = 0;

    // Evaluate transaction result.
    Optional<String> responseErrorMsg = messageService.getTransactInfo().getErrorMessage();
    if (responseErrorMsg.isPresent()) {
      LOGGER.warning(L10N.get("msg.SendPushMessageFailed", responseErrorMsg.get()));
    }
    else {
      LOGGER.info(L10N.get("msg.SendPushMessageSuccess"));

      // Store current price for subsequent calls.
      priceAtLastSentMessage = currentPrice;
    }
  }

  private PetrolStation findCheapestStation(
      List<PetrolStation> stations,
      PetrolType preferredPetrolType) {

    // Find cheapest station according to our business rules.
    Optional<PetrolStation> cheapestStation = PetrolStations
        .findCheapest(stations, preferredPetrolType);

    if (!cheapestStation.isPresent()) {
      String errMessage = "Did not find cheapest petrol station but expected to find one.";
      LOGGER.severe(errMessage);
      throw new IllegalStateException(errMessage);
    }

    return cheapestStation.get();
  }

  private double findPrice(PetrolStation cheapestStation, PetrolType preferredPetrolType) {
    return Petrols
        .findPetrol(cheapestStation.getPetrols(), preferredPetrolType)
        .map(petrol -> petrol.price).orElse(0.0);
  }

  private boolean mustSend(double price) {
    boolean isPriceReduction = price > 0.0 && price < priceAtLastSentMessage;
    boolean exceededMaxCallsUntilForceSend =
        callsSinceLastMessage >= maxRequestsUntilForceSendIfPriceIsRisingSinceLastMsg;

    LOGGER.fine("Message send check. Price reduced? " + isPriceReduction);
    LOGGER.fine("Message send check. Exceeded max. calls until force send? "
        + exceededMaxCallsUntilForceSend);

    return isPriceReduction || exceededMaxCallsUntilForceSend;
  }
}
