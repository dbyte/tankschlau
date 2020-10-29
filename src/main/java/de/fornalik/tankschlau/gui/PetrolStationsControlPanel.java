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

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.service.PetrolStationsWorker;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.WorkerService;
import de.fornalik.tankschlau.webserviceapi.common.PetrolStationMessageWorker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Panel with interactive Components, driving worker service and data table model.
 */
class PetrolStationsControlPanel extends JPanel implements ActionListener {

  private static final Logger LOGGER = Logger.getLogger(FooterPanel.class.getName());
  private static final Localization L10N = Localization.getInstance();

  private final WorkerService<List<PetrolStation>> workerService;
  private final PetrolStationMessageWorker messageWorker;
  private final UserPrefs userPrefs;
  private final JButton btnStartOneShotWork, btnStartCyclicWork, btnRemoveAllData;
  private final PetrolsStationsTableModel dataTableModel;
  private final FooterPanel footerPanel;

  PetrolStationsControlPanel(
      PetrolsStationsTableModel dataTableModel,
      FooterPanel footerPanel,
      UserPrefs userPrefs,
      WorkerService<List<PetrolStation>> petrolStationsWorkerService,
      PetrolStationMessageWorker messageWorker) {

    this.workerService = petrolStationsWorkerService;
    this.messageWorker = messageWorker;

    this.userPrefs = userPrefs;
    this.dataTableModel = dataTableModel;
    this.footerPanel = footerPanel;
    this.btnStartOneShotWork = new JButton();
    this.btnStartCyclicWork = new JButton();
    this.btnRemoveAllData = new JButton();

    this.initView();
  }

  private void initView() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setAlignmentX(Component.CENTER_ALIGNMENT);
    setPreferredSize(new Dimension(190, this.getHeight()));
    setMaximumSize(new Dimension(190, Short.MAX_VALUE));

    add(Box.createRigidArea(new Dimension(getMaximumSize().width, 52)));
    addButton(btnStartOneShotWork, this, L10N.get("button.UpdateOnce"));

    add(Box.createRigidArea(new Dimension(getMaximumSize().width, 5)));
    addButton(btnStartCyclicWork, this, L10N.get("button.UpdateCyclic"));

    add(Box.createRigidArea(new Dimension(getMaximumSize().width, 5)));
    addButton(btnRemoveAllData, this, L10N.get("button.EmptyTableView"));

    add(Box.createRigidArea(new Dimension(getMaximumSize().width, 10)));
    add(createSeparator());
    add(createPetrolTypeChooser());
    add(Box.createVerticalGlue());
  }

  private void addButton(JButton btn, ActionListener listener, String title) {
    btn.setText(title);
    btn.addActionListener(listener);
    btn.setMinimumSize(new Dimension(getMaximumSize().width, 25));
    btn.setMaximumSize(new Dimension(getMaximumSize().width, 25));
    btn.setPreferredSize(new Dimension(getMaximumSize().width, 25));
    btn.setForeground(CustomColor.BUTTON_FOREGROUND);
    btn.setFocusable(false);
    btn.setAlignmentX(CENTER_ALIGNMENT);
    add(btn);
  }

  private JPanel createPetrolTypeChooser() {
    PetrolTypePanel panel = new PetrolTypePanel(userPrefs);
    panel.setMinimumSize(new Dimension(getMaximumSize().width, 100));
    panel.setMaximumSize(new Dimension(getMaximumSize().width, 100));
    panel.setPreferredSize(new Dimension(getMaximumSize().width, 100));
    panel.setAlignmentX(CENTER_ALIGNMENT);

    return panel;
  }

  private JSeparator createSeparator() {
    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
    separator.setAlignmentX(CENTER_ALIGNMENT);
    separator.setMinimumSize(new Dimension(getMaximumSize().width, 10));
    separator.setMaximumSize(new Dimension(getMaximumSize().width, 10));

    return separator;
  }

  private void onCyclicWorkerStarted() {
    SwingUtilities.invokeLater(() -> {
      btnStartCyclicWork.setActionCommand("STOP");
      btnStartCyclicWork.setText(L10N.get("button.StopCycling"));
      btnStartOneShotWork.setEnabled(false);
    });
  }

  private void onCyclicWorkerStopped() {
    SwingUtilities.invokeLater(() -> {
      btnStartCyclicWork.setActionCommand("START");
      btnStartCyclicWork.setText(L10N.get("button.UpdateCyclic"));
      btnStartOneShotWork.setEnabled(true);
      footerPanel.onCyclicWorkerStopped();
    });
  }

  private void onSingleCycleFinished(List<PetrolStation> petrolStations) {
    SwingUtilities.invokeLater(() -> dataTableModel.addPetrolStations(petrolStations));
    messageWorker.checkSendMessage(petrolStations, userPrefs.readPreferredPetrolType());
  }

  private void onOneShotWorkerStarted() {
    SwingUtilities.invokeLater(() -> {
      btnStartCyclicWork.setEnabled(false);
      btnStartOneShotWork.setEnabled(false);
      footerPanel.onOneShotWorkerStarted(L10N.get("msg.PetrolStationRequestRunning"));
    });
  }

  private void onOneShotWorkerFinished(List<PetrolStation> petrolStations) {
    SwingUtilities.invokeLater(() -> {
      dataTableModel.addPetrolStations(petrolStations);
      btnStartCyclicWork.setEnabled(true);
      btnStartOneShotWork.setEnabled(true);
      footerPanel.onOneShotWorkerFinished();
    });
  }

  private void updateCountdown(long remaining, TimeUnit timeUnit) {
    SwingUtilities.invokeLater(() -> footerPanel.updateCountdown(remaining, timeUnit));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (e.getSource() == btnStartOneShotWork) {
      ((PetrolStationsWorker) workerService.getWorker()).setUserGeo(getUserGeo());
      workerService.startOneShot(this::onOneShotWorkerFinished);
      onOneShotWorkerStarted();
    }

    if (e.getSource() == btnStartCyclicWork) {
      String actionCommand = btnStartCyclicWork.getActionCommand();

      if (!"STOP".equals(actionCommand)) {
        ((PetrolStationsWorker) workerService.getWorker()).setUserGeo(getUserGeo());
        // Start a cycle every x seconds
        workerService.setTimeUnit(TimeUnit.SECONDS);
        workerService.startCyclic(
            this::onSingleCycleFinished,
            userPrefs.readPetrolStationsUpdateCycleRate());

        workerService.processCountdown((remaining) ->
            this.updateCountdown(remaining, workerService.getTimeUnit()));

        onCyclicWorkerStarted();
      }
      else {
        workerService.stopCyclic();
        onCyclicWorkerStopped();
      }
    }

    else if (e.getSource() == btnRemoveAllData) {
      dataTableModel.removeAllPetrolStations();
    }
  }

  private Geo getUserGeo() {
    Optional<Geo> geo = userPrefs.readGeo();

    if (!geo.isPresent()) {
      String errMessage = L10N.get("msg.UnableToRequestPetrolStations_ReasonNoGeoForUser");
      LOGGER.warning(errMessage);
      throw new IllegalStateException(errMessage);
    }

    return geo.get();
  }
}
