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
  private static final Localization L10N = Localization.getInstance();
  private static final Dimension DEFAULT_WINDOW_DIMENSION = new Dimension(1400, 900);

  private final MainView mainView;
  private final PrefsView prefsView;
  private final FooterView footerView;

  @Autowired
  public MainWindow(MainView mainView, PrefsView prefsView, FooterView footerView) {
    super(Localization.APP_NAME);
    this.mainView = mainView;
    this.prefsView = prefsView;
    this.footerView = footerView;
  }

  public void initView() {
    LOGGER.finest("Loading initial view");

    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

    add(createTabbedPane());

    configureFooterPanel();
    add(footerView);

    setBounds(50, 50, DEFAULT_WINDOW_DIMENSION.width, DEFAULT_WINDOW_DIMENSION.height);
    pack();
    setVisible(true);
  }

  private void configureFooterPanel() {
    footerView.setPreferredSize(new Dimension(0, 40));
    footerView.setMinimumSize(new Dimension(getWidth(), 40));
    footerView.setMaximumSize(new Dimension(getMaximumSize().width, 40));
  }

  private JTabbedPane createTabbedPane() {
    JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.addTab(L10N.get("tab.PetrolPrices"), mainView);
    tabbedPane.addTab(L10N.get("tab.Preferences"), prefsView);
    tabbedPane.setSelectedIndex(0);

    tabbedPane.setPreferredSize(DEFAULT_WINDOW_DIMENSION);
    tabbedPane.setTabPlacement(SwingConstants.TOP);
    tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
    tabbedPane.setBorder(BorderFactory.createEmptyBorder());
    tabbedPane.setOpaque(true);

    return tabbedPane;
  }
}
