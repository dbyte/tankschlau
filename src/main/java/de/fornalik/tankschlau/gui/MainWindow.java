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
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.WorkerService;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyStore;

import javax.swing.*;
import java.awt.*;
import java.util.List;


/**
 * The app's main JFrame.
 */
class MainWindow extends JFrame {
  private static final Localization L10N = Localization.getInstance();
  private static final Dimension DEFAULT_WINDOW_DIMENSION = new Dimension(1200, 800);
  private final JPanel domainPanelTab, prefsPanelTab;
  private final JPanel footerPanel;

  MainWindow(
      UserPrefs userPrefs,
      ApiKeyStore apiKeyStore,
      WorkerService<List<PetrolStation>> petrolStationsWorkerService,
      WorkerService<Geo> geocodingWorkerService) {

    super(Localization.APP_NAME);

    this.footerPanel = new FooterPanel();

    this.domainPanelTab = new MainPanel(
        (FooterPanel) this.footerPanel,
        userPrefs,
        petrolStationsWorkerService);

    this.prefsPanelTab = new PrefsPanel(
        userPrefs,
        apiKeyStore,
        geocodingWorkerService,
        (FooterPanel) this.footerPanel);

    this.initView();
  }

  private void initView() {
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    configureFooterPanel();

    add(createTabbedPane());
    add(footerPanel);

    setBounds(100, 100, DEFAULT_WINDOW_DIMENSION.width, DEFAULT_WINDOW_DIMENSION.height);
    pack();
    setVisible(true);
  }

  private void configureFooterPanel() {
    footerPanel.setPreferredSize(new Dimension(0, 40));
    footerPanel.setMinimumSize(new Dimension(getWidth(), 40));
    footerPanel.setMaximumSize(new Dimension(getMaximumSize().width, 40));
  }

  private JTabbedPane createTabbedPane() {
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab(L10N.get("tab.PetrolPrices"), domainPanelTab);
    tabbedPane.addTab(L10N.get("tab.Preferences"), prefsPanelTab);
    tabbedPane.setSelectedIndex(0);

    tabbedPane.setPreferredSize(DEFAULT_WINDOW_DIMENSION);
    tabbedPane.setTabPlacement(JTabbedPane.TOP);
    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
    tabbedPane.setBorder(BorderFactory.createEmptyBorder());
    tabbedPane.setOpaque(true);

    return tabbedPane;
  }
}
