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
import de.fornalik.tankschlau.gui.window.MainWindow;

import javax.swing.*;
import java.util.Optional;

public final class TankSchlau {
  final AppContainer container = new AppContainer();

  public static void main(String[] args) {
    final TankSchlau instance = new TankSchlau();
    instance.processVmOptions();
    instance.invokeGui();
  }

  private void invokeGui() {
    SwingUtilities.invokeLater(
        () -> new MainWindow(
            container.L10N,
            container.USER_PREFS,
            container.PETROL_STATIONS_WEBSERVICE,
            container.GEOCODING_CLIENT,
            container.MESSAGE_CLIENT,
            container.PETROL_STATION_MESSAGE_CONTENT));
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
}