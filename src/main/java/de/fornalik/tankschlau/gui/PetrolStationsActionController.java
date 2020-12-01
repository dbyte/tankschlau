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

import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.util.Localization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Controller
class PetrolStationsActionController {

  private static final Localization L10N = Localization.getInstance();
  private static final String CMD_START_CYCLIC = "START";
  private static final String CMD_STOP_CYCLIC = "STOP";

  private final PetrolStationsActionModel model;
  private final PetrolStationsActionView view;
  private final PetrolsStationsTableModel tableModel;
  private final FooterController footerController;

  @Autowired
  PetrolStationsActionController(
      PetrolStationsActionModel model,
      PetrolStationsActionView view,
      PetrolsStationsTableModel tableModel,
      FooterController footerController) {

    this.model = model;
    this.view = view;
    this.tableModel = tableModel;
    this.footerController = footerController;
  }

  @PostConstruct
  private void registerActionListeners() {
    ActionListener buttonListener = new ButtonListener();

    view.getBtnStartCyclicWork().addActionListener(buttonListener);
    view.getBtnStartOneShotWork().addActionListener(buttonListener);
    view.getBtnRemoveAllData().addActionListener(buttonListener);
  }

  private void onCyclicWorkerStarted() {
    SwingUtilities.invokeLater(() -> {
      view.getBtnStartCyclicWork().setActionCommand(CMD_STOP_CYCLIC);
      view.getBtnStartCyclicWork().setText(L10N.get("button.StopCycling"));
      view.getBtnStartOneShotWork().setEnabled(false);
    });
  }

  private void onCyclicWorkerStopped() {
    SwingUtilities.invokeLater(() -> {
      view.getBtnStartCyclicWork().setActionCommand(CMD_START_CYCLIC);
      view.getBtnStartCyclicWork().setText(L10N.get("button.UpdateCyclic"));
      view.getBtnStartOneShotWork().setEnabled(true);
      footerController.onCyclicWorkerStopped();
    });
  }

  private void onSingleCycleFinished(List<PetrolStation> petrolStations) {
    SwingUtilities.invokeLater(() -> tableModel.addPetrolStations(petrolStations));
    model.sendPushmessage(petrolStations);
  }

  private void onOneShotWorkerStarted() {
    SwingUtilities.invokeLater(() -> {
      view.getBtnStartCyclicWork().setEnabled(false);
      view.getBtnStartOneShotWork().setEnabled(false);
      footerController.onOneShotWorkerStarted(L10N.get("msg.PetrolStationRequestRunning"));
    });
  }

  private void onOneShotWorkerFinished(List<PetrolStation> petrolStations) {
    SwingUtilities.invokeLater(() -> {
      tableModel.addPetrolStations(petrolStations);
      view.getBtnStartCyclicWork().setEnabled(true);
      view.getBtnStartOneShotWork().setEnabled(true);
      footerController.onOneShotWorkerFinished();
    });
  }

  private void updateCountdown(long remaining, TimeUnit timeUnit) {
    SwingUtilities.invokeLater(() -> footerController.updateCountdown(remaining, timeUnit));
  }

  private class ButtonListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {

      // Update petrol stations once.
      if (e.getSource() == view.getBtnStartOneShotWork()) {
        model.updatePetrolStations(PetrolStationsActionController.this::onOneShotWorkerFinished);
        onOneShotWorkerStarted();
      }

      else if (e.getSource() == view.getBtnStartCyclicWork()) {
        String actionCommand = view.getBtnStartCyclicWork().getActionCommand();

        // Start automatic update of petrol stations every X time units.
        if (!CMD_STOP_CYCLIC.equals(actionCommand)) {
          model.startCyclicUpdatePetrolStations(
              PetrolStationsActionController.this::onSingleCycleFinished);

          model.startObservingCycleCountdown(PetrolStationsActionController.this::updateCountdown);
          onCyclicWorkerStarted();
        }

        // Stop automatic updates of petrol stations.
        else {
          model.stopCyclicUpdatePetrolStations();
          onCyclicWorkerStopped();
        }
      }

      // Clear contents of petrol station table.
      else if (e.getSource() == view.getBtnRemoveAllData()) {
        tableModel.removeAllPetrolStations();
      }
    }
  }
}
