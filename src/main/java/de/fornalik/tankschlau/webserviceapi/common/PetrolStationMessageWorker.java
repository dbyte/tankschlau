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
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

  private final ExecutorService executor;
  private final MessageService messageService;
  private final PetrolStationMessageContent messageContent;
  private final UserPrefs userPrefs;

  public PetrolStationMessageWorker(
      MessageService messageService,
      PetrolStationMessageContent messageContent,
      UserPrefs userPrefs) {

    this.messageService = Objects.requireNonNull(messageService);
    this.messageContent = Objects.requireNonNull(messageContent);
    this.userPrefs = userPrefs;

    this.executor = Executors.newSingleThreadExecutor();
  }

  /**
   * Execute checking/sending of a petrol station push message.
   */
  public synchronized void execute(List<PetrolStation> stations, PetrolType preferredPetrolType) {
    if (!userPrefs.readPushMessageEnabled()) {
      LOGGER.finer("Push messages disabled, skipping.");
      return;
    }

    executor.execute(() -> checkSendMessage(stations, preferredPetrolType));
  }

  /**
   * @param stations            List of petrol station in which to find the station of interest.
   * @param preferredPetrolType Type of petrol the message receiver is interested in.
   * @see PetrolStations#sortByPriceAndDistanceForPetrolType(List, PetrolType)
   */
  private void checkSendMessage(
      List<PetrolStation> stations,
      PetrolType preferredPetrolType) {

    if (stations == null || stations.size() == 0) {
      LOGGER.fine("Given stations are null or empty, so no push message to send.");
      return;
    }

    callsSinceLastMessage++;
    LOGGER.finer("Valid calls since last message: " + callsSinceLastMessage);

    /*if (callsSinceLastMessage == 1) {
      // Test thread crash... no unit tests yet.
      int divisionByZero = 1 / 0;
    }*/

    PetrolStation cheapestStation = findCheapestStation(stations, preferredPetrolType);
    double currentPrice = findPrice(cheapestStation, preferredPetrolType);

    if (!mustSend(currentPrice))
      return;

    doSendMessage(cheapestStation, preferredPetrolType, currentPrice);
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

    // Evaluate transaction result.
    Optional<String> responseErrorMsg = messageService.getTransactInfo().getErrorMessage();
    if (responseErrorMsg.isPresent()) {
      LOGGER.warning(L10N.get("msg.SendPushMessageFailed", responseErrorMsg.get()));
    }
    else {
      LOGGER.info(L10N.get("msg.SendPushMessageSuccess"));

      // Update internal values for subsequent calls.
      priceAtLastSentMessage = currentPrice;
      callsSinceLastMessage = 0;
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

  private boolean mustSend(double currentPrice) {
    int maxCallsUntilForceSendIfPriceHasRisenSinceLastMessage = userPrefs
        .readPushMessageMaxCallsUntilForceSend();

    /*
    If the current price is reduced in comparison to the one when the last message was sent,
    we want the message to be sent, regardless to the number of invokes of this worker.
     */
    boolean isPriceReduction = currentPrice > 0.0 && currentPrice < priceAtLastSentMessage;

    /*
    Only true when (_additionally_ to exceeding max. calls) the current price is greater than it
    was at the time the last message was sent! This is because we do not want to force informing
    the user about a price that is equal to the one in the last sent message.
    If the current price is reduced in comparison to the one when the last message was sent,
    variable isPriceReduction would be true anyway and force the message to be sent.
     */
    boolean exceededMaxCallsUntilForceSendAndPriceHasRisen =
        callsSinceLastMessage >= maxCallsUntilForceSendIfPriceHasRisenSinceLastMessage
            && currentPrice > priceAtLastSentMessage;

    boolean mustSend = isPriceReduction || exceededMaxCallsUntilForceSendAndPriceHasRisen;

    LOGGER.finer("Message send check. Price reduced? " + isPriceReduction);
    LOGGER.finer("Message send check. Exceeded max. calls until force send and price has risen? "
        + exceededMaxCallsUntilForceSendAndPriceHasRisen);
    LOGGER.fine("Must send message? " + mustSend);

    return mustSend;
  }
}
