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

import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyStore;

import javax.swing.*;
import java.awt.*;

/**
 * The app's main preferences panel.
 */
class PrefsPanel extends JPanel {
  private final JPanel prefsCyclePanel;
  private final JPanel prefsAddressPanel;
  private final JPanel prefsApiKeyPanel;

  PrefsPanel(UserPrefs userPrefs, ApiKeyStore apiKeyStore, FooterPanel footerPanel) {
    this.prefsCyclePanel = new PrefsCyclePanel(userPrefs);
    this.prefsAddressPanel = new PrefsAddressPanel(userPrefs, footerPanel);
    this.prefsApiKeyPanel = new PrefsApiKeyPanel(apiKeyStore);
    this.initView();
  }

  private void initView() {
    setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

    prefsCyclePanel.setAlignmentY(TOP_ALIGNMENT);
    prefsAddressPanel.setAlignmentY(TOP_ALIGNMENT);
    prefsApiKeyPanel.setAlignmentY(TOP_ALIGNMENT);

    add(prefsCyclePanel);
    add(Box.createHorizontalGlue());
    add(Box.createRigidArea(new Dimension(20, 0)));
    add(prefsAddressPanel);
    add(Box.createHorizontalGlue());
    add(Box.createRigidArea(new Dimension(20, 0)));
    add(prefsApiKeyPanel);
  }
}
