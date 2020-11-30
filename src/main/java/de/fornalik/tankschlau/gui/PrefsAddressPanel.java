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
 * User preferences panel for user location
 */
@org.springframework.stereotype.Component
class PrefsAddressPanel extends JPanel implements PrefsFactoryMixin {

  private static final Dimension DEFAULT_SIZE = new Dimension(350, 325);

  private final JTextField textStreet;
  private final JTextField textHouseNumber;
  private final JTextField textPostCode;
  private final JTextField textCity;
  private final JTextField textGeoLatitude;
  private final JTextField textGeoLongitude;
  private final JTextField textSearchRadius;
  private final JButton btnGeoRequest;

  private final Localization l10n;

  @Autowired
  PrefsAddressPanel(Localization l10n) {
    super();
    this.l10n = l10n;

    this.textStreet = createTextField();
    this.textHouseNumber = createTextField();
    this.textPostCode = createIntegerOnlyTextField(5); // German postcodes
    this.textCity = createTextField();
    this.textGeoLatitude = createIntegerOrFloatOnlyTextField(20);
    this.textGeoLongitude = createIntegerOrFloatOnlyTextField(20);
    this.textSearchRadius = createIntegerOrFloatOnlyTextField(5);

    this.btnGeoRequest = createGeoRequestButton();
  }

  @PostConstruct
  private void initView() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setAlignmentX(LEFT_ALIGNMENT);
    setOpaque(true);
    setBorder(createTitledBorder(l10n.get("borderTitle.YourLocation")));
    setPreferredSize(DEFAULT_SIZE);
    setMaximumSize(DEFAULT_SIZE);
    setMinimumSize(DEFAULT_SIZE);

    add(createAddressFieldsPanel());
    add(Box.createRigidArea(new Dimension(0, 5)));
    add(createSeparator());
    add(createGeoFieldsPanel());
    add(Box.createRigidArea(new Dimension(0, 5)));
    add(btnGeoRequest);
    // ---> Google Logo panel gets inserted here if Google GeoService in current GeoService
    // implementation, see insertPoweredByGooglePanel() <---
    add(Box.createRigidArea(new Dimension(0, 5)));
    add(createSeparator());
    add(createDistancePanel());
  }

  private JPanel createAddressFieldsPanel() {
    JPanel panel = createGridPanel(4);

    panel.add(createLabel(l10n.get("label.AdrStreet")));
    panel.add(textStreet);

    panel.add(createLabel(l10n.get("label.AdrHouseNumber")));
    panel.add(textHouseNumber);

    panel.add(createLabel(l10n.get("label.AdrPostcode")));
    panel.add(textPostCode);

    panel.add(createLabel(l10n.get("label.AdrCity")));
    panel.add(textCity);

    return panel;
  }

  private JPanel createGeoFieldsPanel() {
    JPanel panel = createGridPanel(2);

    panel.add(createLabel(l10n.get("label.AdrLatitude")));
    panel.add(textGeoLatitude);

    panel.add(createLabel(l10n.get("label.AdrLongitude")));
    panel.add(textGeoLongitude);

    return panel;
  }

  private JPanel createDistancePanel() {
    JPanel panel = createGridPanel(1);

    panel.add(createLabel(l10n.get("label.PetrolStationsSearchRadius")));
    panel.add(textSearchRadius);

    return panel;
  }

  private JButton createGeoRequestButton() {
    JButton btnRequestGeo = new JButton(l10n.get("button.EvaluateLatLonByAddress"));
    btnRequestGeo.setAlignmentX(LEFT_ALIGNMENT);
    btnRequestGeo.setMinimumSize(new Dimension(DEFAULT_SIZE.width - 10, 25));
    btnRequestGeo.setMaximumSize(new Dimension(DEFAULT_SIZE.width - 10, 25));
    btnRequestGeo.setPreferredSize(new Dimension(DEFAULT_SIZE.width - 10, 25));
    btnRequestGeo.setForeground(CustomColor.BUTTON_FOREGROUND);
    btnRequestGeo.setFocusable(false);

    return btnRequestGeo;
  }

  void insertPoweredByGooglePanel() {
    // Should be conditionally called by the controller if Google GeoService is the active used
    // implementation asking for lat/lon data. Insert copyright logo:
    JPanel panel = new PoweredByGooglePanel();
    final int indexPositionInParentBoxLayout = 7;

    panel.setAlignmentX(LEFT_ALIGNMENT);
    panel.setMinimumSize(new Dimension(DEFAULT_SIZE.width, 17));
    panel.setMaximumSize(new Dimension(DEFAULT_SIZE.width, 17));
    panel.setPreferredSize(new Dimension(DEFAULT_SIZE.width, 17));

    add(Box.createRigidArea(new Dimension(0, 5)));
    add(panel, indexPositionInParentBoxLayout);
  }

  private JPanel createGridPanel(int rows) {
    JPanel panel = new JPanel(new GridLayout(rows, 2));
    panel.setAlignmentX(LEFT_ALIGNMENT);
    panel.setPreferredSize(new Dimension(DEFAULT_SIZE.width, rows * 25 + 4 * rows));
    panel.setMinimumSize(new Dimension(DEFAULT_SIZE.width, rows * 25 + 4 * rows));
    panel.setMaximumSize(new Dimension(DEFAULT_SIZE.width, rows * 25 + 4 * rows));

    return panel;
  }

  private JSeparator createSeparator() {
    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
    separator.setAlignmentX(LEFT_ALIGNMENT);
    separator.setMaximumSize(new Dimension(DEFAULT_SIZE.width, 13));

    return separator;
  }

  JTextField getTextStreet() {
    return textStreet;
  }

  JTextField getTextHouseNumber() {
    return textHouseNumber;
  }

  JTextField getTextPostCode() {
    return textPostCode;
  }

  JTextField getTextCity() {
    return textCity;
  }

  JTextField getTextGeoLatitude() {
    return textGeoLatitude;
  }

  JTextField getTextGeoLongitude() {
    return textGeoLongitude;
  }

  JTextField getTextSearchRadius() {
    return textSearchRadius;
  }

  JButton getBtnGeoRequest() {
    return btnGeoRequest;
  }
}
