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

import de.fornalik.tankschlau.gui.SwingBootstrap;

import java.util.Optional;
import java.util.logging.Logger;

public final class TankSchlau {
  private static Logger logger;
  private static TankSchlauBootstrap container;

  public static void main(String[] args) {
    container = new TankSchlauBootstrap();
    logger = Logger.getLogger(TankSchlau.class.getName());

    final TankSchlau instance = new TankSchlau();
    instance.processVmOptions();
    instance.startSwingApplication();
  }

  private void startSwingApplication() {
    logger.finest("Invoking GUI");

    new SwingBootstrap(
        container.userPrefs,
        container.apiKeyStore,
        container.petrolStationsWorker);
  }

  private void processVmOptions() {
    // Offer option to pass some data at startup. Ex: -Dmyproperty="My value"

    logger.finest("Processing VM options");

    Optional.ofNullable(System.getProperty("petrolStationsApiKey"))
        .ifPresent(container.tankerkoenigApikeyManager::write);

    Optional.ofNullable(System.getProperty("geocodingApiKey"))
        .ifPresent(container.geocodingApikeyManager::write);

    Optional.ofNullable(System.getProperty("pushmessageApiKey"))
        .ifPresent(container.apiKeyManager::write);

    Optional.ofNullable(System.getProperty("pushmessageUserId"))
        .ifPresent(container.userPrefs::writePushMessageUserId);
  }
}