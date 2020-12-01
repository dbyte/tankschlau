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

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * User preferences panel for authorization keys of needed webservices.
 */
@org.springframework.stereotype.Component
public class PrefsApiKeyView extends JPanel implements PrefsFactoryMixin {

  private static final Localization L10N = Localization.getInstance();
  private static final Dimension DEFAULT_SIZE = new Dimension(440, 138);

  private final JPasswordField textPetrolStationsServiceApiKey;
  private final JPasswordField textGeocodingServiceApiKey;
  private final JPasswordField textMessageServiceApiKey;
  private final JPasswordField textMessageServiceUserKey;

  @Autowired
  public PrefsApiKeyView() {
    super();
    this.textPetrolStationsServiceApiKey = createPasswordField();
    this.textGeocodingServiceApiKey = createPasswordField();
    this.textMessageServiceApiKey = createPasswordField();
    this.textMessageServiceUserKey = createPasswordField();
  }

  @PostConstruct
  private void initView() {
    setLayout(new GridLayout(4, 2));
    setAlignmentX(Component.LEFT_ALIGNMENT);
    setOpaque(true);
    setBorder(createTitledBorder(L10N.get("borderTitle.Authentication")));
    setPreferredSize(DEFAULT_SIZE);
    setMaximumSize(DEFAULT_SIZE);
    setMinimumSize(DEFAULT_SIZE);

    add(createLabel("Tankerkoenig.de API Key"));
    add(textPetrolStationsServiceApiKey);

    add(createLabel("Google Geocoding API Key"));
    add(textGeocodingServiceApiKey);

    add(createLabel("Pushover.net Message API Key"));
    add(textMessageServiceApiKey);
    add(createLabel("Pushover.net Message User Key"));
    add(textMessageServiceUserKey);
  }

  JPasswordField getTextPetrolStationsServiceApiKey() {
    return textPetrolStationsServiceApiKey;
  }

  JPasswordField getTextGeocodingServiceApiKey() {
    return textGeocodingServiceApiKey;
  }

  JPasswordField getTextMessageServiceApiKey() {
    return textMessageServiceApiKey;
  }

  JPasswordField getTextMessageServiceUserKey() {
    return textMessageServiceUserKey;
  }
}
