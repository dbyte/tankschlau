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
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyManager;
import de.fornalik.tankschlau.webserviceapi.common.ApiKeyStore;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * User preferences panel for API keys of some webservices.
 */
public class PrefsApiKeyPanel extends JPanel implements FocusListener, PrefsFactoryMixin {

  private static final Localization L10N = Localization.getInstance();
  private static final Dimension totalDimension = new Dimension(440, 108);

  private final JPasswordField textPetrolStationsServiceApiKey;
  private final JPasswordField textGeocodingServiceApiKey;
  private final JPasswordField textMessageServiceApiKey;
  private final ApiKeyManager apiKeyManagerPetrolStations;
  private final ApiKeyManager apiKeyManagerGeocoding;
  private final ApiKeyManager apiKeyManagerPushMessage;

  public PrefsApiKeyPanel(ApiKeyStore apiKeyStore) {
    super();

    this.apiKeyManagerPetrolStations = ApiKeyManager.createForPetrolStations(apiKeyStore);
    this.apiKeyManagerGeocoding = ApiKeyManager.createForGeocoding(apiKeyStore);
    this.apiKeyManagerPushMessage = ApiKeyManager.createForPushMessage(apiKeyStore);

    this.textPetrolStationsServiceApiKey = createPasswordField();
    this.textGeocodingServiceApiKey = createPasswordField();
    this.textMessageServiceApiKey = createPasswordField();

    this.initView();
    this.populateFields();
  }

  private void initView() {
    setLayout(new GridLayout(3, 2));
    setAlignmentX(Component.LEFT_ALIGNMENT);
    setOpaque(true);
    setBorder(createTitledBorder(L10N.get("borderTitle.ApiKeys")));
    setPreferredSize(totalDimension);
    setMaximumSize(totalDimension);
    setMinimumSize(totalDimension);

    configureFields();

    add(createLabel(L10N.get("label.TankerkoenigService")));
    add(textPetrolStationsServiceApiKey);

    add(createLabel("Google Geocoding Webservice"));
    add(textGeocodingServiceApiKey);

    add(createLabel("Pushover Message Webservice"));
    add(textMessageServiceApiKey);
  }

  private void configureFields() {
    textPetrolStationsServiceApiKey.addFocusListener(this);
    textGeocodingServiceApiKey.addFocusListener(this);
    textMessageServiceApiKey.addFocusListener(this);
  }

  private void populateFields() {
    textPetrolStationsServiceApiKey.setText(apiKeyManagerPetrolStations.read().orElse(""));
    textGeocodingServiceApiKey.setText(apiKeyManagerGeocoding.read().orElse(""));
    textMessageServiceApiKey.setText(apiKeyManagerPushMessage.read().orElse(""));
  }

  private String getApiKeyFromField(JPasswordField field) {
    return String.valueOf(field.getPassword());
  }

  @Override
  public void focusGained(FocusEvent e) {
    // No need
  }

  @Override
  public void focusLost(FocusEvent e) {
    if (!(e.getSource() instanceof JPasswordField)) return;
    JPasswordField field = (JPasswordField) e.getSource();

    if (field == textPetrolStationsServiceApiKey) {
      apiKeyManagerPetrolStations.write(getApiKeyFromField(textPetrolStationsServiceApiKey));
    }
    else if (field == textGeocodingServiceApiKey) {
      apiKeyManagerGeocoding.write(getApiKeyFromField(textGeocodingServiceApiKey));
    }
    else if (field == textMessageServiceApiKey) {
      apiKeyManagerPushMessage.write(getApiKeyFromField(textMessageServiceApiKey));
    }
  }
}
