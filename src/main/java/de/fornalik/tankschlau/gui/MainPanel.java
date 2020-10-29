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
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.WorkerService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Main content of the app, represented in MainWindow.
 */
class MainPanel extends JPanel {

  private final JPanel logPanel;
  private final JPanel dataPanel;

  MainPanel(
      FooterPanel footerPanel,
      UserPrefs userPrefs,
      WorkerService<List<PetrolStation>> petrolStationsWorkerService) {

    this.dataPanel = new PetrolStationsDataPanel(
        footerPanel,
        userPrefs,
        petrolStationsWorkerService);

    this.logPanel = new LogPanel();

    this.initView();
  }

  private void initView() {
    setLayout(new GridLayout(1, 1));
    setOpaque(true);

    add(createSplitPane());
  }

  private JSplitPane createSplitPane() {
    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
    splitPane.setResizeWeight(0.7); // Top (data area) has greater weight
    splitPane.setBorder(null);
    splitPane.setContinuousLayout(true);
    splitPane.setTopComponent(createTopPanel());
    splitPane.setBottomComponent(createBottomPanel());
    //<0 : Reset to a value that attempts to honor the preferred size of the left/top component:
    splitPane.setDividerLocation(-1);

    return splitPane;
  }

  private JPanel createTopPanel() {
    JPanel panel = new JPanel();
    panel.add(dataPanel);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    return panel;
  }

  private JPanel createBottomPanel() {
    JPanel panel = new JPanel();
    panel.add(logPanel);
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    return panel;
  }
}
