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

import javax.swing.*;
import java.awt.*;

/**
 * User preferences panel for API keys of some webservices.
 */
public class PrefsApiKeyPanel extends JPanel implements PrefsFactoryMixin {

  private static final Dimension totalDimension = new Dimension(380, 108);

  private final JPasswordField textPetrolStationsServiceApiKey;
  private final JPasswordField textGeocodingServiceApiKey;
  private final JPasswordField textMessageServiceApiKey;

  public PrefsApiKeyPanel(UserPrefs userPrefs) {
    super();

    this.textPetrolStationsServiceApiKey = new JPasswordField();
    this.textGeocodingServiceApiKey = new JPasswordField();
    this.textMessageServiceApiKey = new JPasswordField();

    this.initView();
  }

  private void initView() {
    setLayout(new GridLayout(3, 2));
    setAlignmentX(Component.LEFT_ALIGNMENT);
    setOpaque(true);
    setBorder(createTitledBorder("API Keys"));
    setPreferredSize(totalDimension);
    setMaximumSize(totalDimension);
    setMinimumSize(totalDimension);

    add(createLabel("Petrol Station Service"));
    add(textPetrolStationsServiceApiKey);

    add(createLabel("Geocoding Service"));
    add(textGeocodingServiceApiKey);

    add(createLabel("Push Message Service"));
    add(textMessageServiceApiKey);
  }
}
