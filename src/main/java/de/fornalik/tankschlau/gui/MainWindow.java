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

import de.fornalik.tankschlau.util.Localization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * The app's main window.
 */
@Component
public class MainWindow extends JFrame {
  private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());
  private static final Dimension DEFAULT_WINDOW_DIMENSION = new Dimension(1400, 900);

  private final Localization l10n;
  private final MainPanel domainPanelTab;
  private final PrefsPanel prefsPanelTab;
  private final FooterPanel footerPanel;

  @Autowired
  public MainWindow(
      Localization l10n,
      FooterPanel footerPanel,
      MainPanel domainPanelTab,
      PrefsPanel prefsPanelTab) {

    super(Localization.APP_NAME);
    this.l10n = l10n;
    this.footerPanel = footerPanel;
    this.domainPanelTab = domainPanelTab;
    this.prefsPanelTab = prefsPanelTab;
  }

  public void initView() {
    LOGGER.finest("Loading initial view");

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    add(createTabbedPane());

    configureFooterPanel();
    add(footerPanel);

    setBounds(50, 50, DEFAULT_WINDOW_DIMENSION.width, DEFAULT_WINDOW_DIMENSION.height);
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
    tabbedPane.addTab(l10n.get("tab.PetrolPrices"), domainPanelTab);
    tabbedPane.addTab(l10n.get("tab.Preferences"), prefsPanelTab);
    tabbedPane.setSelectedIndex(0);

    tabbedPane.setPreferredSize(DEFAULT_WINDOW_DIMENSION);
    tabbedPane.setTabPlacement(SwingConstants.TOP);
    tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
    tabbedPane.setBorder(BorderFactory.createEmptyBorder());
    tabbedPane.setOpaque(true);

    return tabbedPane;
  }
}
