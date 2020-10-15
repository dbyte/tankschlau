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

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.Petrol;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.util.Localization;

import java.util.Optional;

/**
 * Abstract implementation of {@link MessageContent} for (push) message content regarding
 * a {@link PetrolStation}.
 */
public abstract class PetrolStationMessageContent implements MessageContent {
  private final Localization l10n;

  protected PetrolStationMessageContent(Localization l10n) {
    this.l10n = l10n;
  }

  /**
   * Formats a ready-to-use push message text.
   *
   * @param station    Petrol station that drives the message content.
   * @param petrolType Petrol type for which to show the current price regarding the given station.
   */
  public void setMessage(PetrolStation station, PetrolType petrolType) {
    String stationHeader = createStationHeader(station);
    String petrol = l10n.get("msg.BestPrice", createPetrolString(station, petrolType));
    String distance = createDistanceString(station);
    String street = station.getAddress().getStreetAndHouseNumber();

    this.setMessage(petrol + "\n" + distance + "\n\n" + stationHeader + "\n" + street);
  }

  private String createStationHeader(PetrolStation station) {
    String stationName = station.getAddress().getName();
    String open = station.isOpen()
        ? l10n.get("msg.NowOpen")
        : l10n.get("msg.NowClosed");

    return stationName + " - " + open;
  }

  private String createPetrolString(PetrolStation station, PetrolType type) {
    String msg = l10n
        .get("msg.NoPetrolDataForStation", type.name(), station.getAddress().getName());

    return station
        .findPetrol(type)
        .map(Petrol::getTypeAndPrice)
        .orElse(msg);
  }

  private String createDistanceString(PetrolStation station) {
    Optional<Geo> geo = station.getAddress().getGeo();

    return geo.isPresent()
        ? geo.get().getDistanceAwayString()
        : l10n.get("msg.Unknown");
  }
}
