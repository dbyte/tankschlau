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

import de.fornalik.tankschlau.service.GeocodingWorker;
import de.fornalik.tankschlau.service.PetrolStationsWorker;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyStore;
import de.fornalik.tankschlau.webserviceapi.common.PetrolStationMessageWorker;

import javax.swing.*;

/**
 * Describes the dependency graph throughout the Swing GUI.
 * Avoid tight coupling to any classes by ONLY calling it's members from the root of the Swing GUI.
 */
public final class SwingBootstrap {

  public SwingBootstrap(
      final UserPrefs userPrefs,
      final ApiKeyStore apiKeyStore,
      final PetrolStationsWorker petrolStationsWorker,
      final GeocodingWorker geocodingWorker,
      final PetrolStationMessageWorker messageWorker) {

    SwingUtilities.invokeLater(() -> new MainWindow(
        userPrefs,
        apiKeyStore,
        new SwingWorkerService<>(petrolStationsWorker),
        new SwingWorkerService<>(geocodingWorker),
        messageWorker
    ));
  }
}
