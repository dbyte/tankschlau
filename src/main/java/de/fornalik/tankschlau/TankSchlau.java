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

import de.fornalik.tankschlau.bootstrap.AppContainer;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.gui.window.MainWindow;
import de.fornalik.tankschlau.station.PetrolType;

import javax.swing.*;
import java.io.IOException;
import java.util.Optional;

public final class TankSchlau {
  final AppContainer container = new AppContainer();

  public static void main(String[] args) {
    final TankSchlau instance = new TankSchlau();
    instance.processVmOptions();
    instance.invokeGui();
  }

  private void invokeGui() {
    // processTestAddress(); // Ex: Writing some user geo data to user prefs

    final Geo userGeo = container.USER_PREFS
        .readGeo()
        .orElseThrow(() -> new IllegalStateException("No preferences found for user geo data."));

    SwingUtilities.invokeLater(
        () -> {
          MainWindow mainWindow = new MainWindow(container.PETROL_STATIONS_DAO);
          mainWindow.initGui();
          mainWindow.updateList(userGeo, PetrolType.DIESEL);
        }
    );
  }

  private void processVmOptions() {
    // Offer option to pass some data at startup. Ex: -Dmyproperty="My value"

    Optional.ofNullable(System.getProperty("petrolStationsApiKey"))
            .ifPresent(container.TANKERKOENIG_APIKEY_MANAGER::write);

    Optional.ofNullable(System.getProperty("geocodingApiKey"))
            .ifPresent(container.GEOCODING_APIKEY_MANAGER::write);

    Optional.ofNullable(System.getProperty("pushmessageApiKey"))
            .ifPresent(container.PUSHMESSAGE_APIKEY_MANAGER::write);

    Optional.ofNullable(System.getProperty("pushmessageUserId"))
            .ifPresent(container.USER_PREFS::writePushMessageUserId);
  }

  // Example: Writing some user address and geo data to user prefs.
  private void processTestAddress() {
    Address address = new Address("An den Äckern", "Wolfsburg", "38446");

    try {
      container.GEOCODING_CLIENT.getGeo(address).ifPresent(address::setGeo);
      address.getGeo().ifPresent(g -> g.setDistance(10.0));
    }
    catch (IOException e) {
      e.printStackTrace();
    }

    container.USER_PREFS.writeAddress(address);
  }
}