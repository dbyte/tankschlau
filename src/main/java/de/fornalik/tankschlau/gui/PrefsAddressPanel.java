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

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.service.GeocodingWorker;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.WorkerService;
import de.fornalik.tankschlau.webserviceapi.google.GoogleGeocodingClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.logging.Logger;

/**
 * User preferences panel for user location
 */
@Controller
class PrefsAddressPanel extends JPanel implements FocusListener, PrefsFactoryMixin {

  private static final Logger LOGGER = Logger.getLogger(PrefsAddressPanel.class.getName());
  private static final Dimension totalDimension = new Dimension(350, 325);
  private static final double DEFAULT_SEARCH_RADIUS = 5.0;

  private final transient WorkerService<Geo> workerService;
  private final UserPrefs userPrefs;
  private final Localization l10n;

  private final JTextField textStreet;
  private final JTextField textHouseNumber;
  private final JTextField textPostCode;
  private final JTextField textCity;
  private final JTextField textGeoLatitude;
  private final JTextField textGeoLongitude;
  private final JTextField textSearchRadius;
  private final JButton btnGeoRequest;

  private final transient BtnGeoRequestController btnGeoRequestController;
  private final FooterController footerController;

  @Autowired
  PrefsAddressPanel(
      UserPrefs userPrefs,
      Localization l10n,
      FooterController footerController,
      WorkerService<Geo> geocodingWorkerService) {

    super();

    this.workerService = geocodingWorkerService;
    this.userPrefs = userPrefs;
    this.l10n = l10n;
    this.footerController = footerController;

    this.textStreet = createTextField();
    this.textHouseNumber = createTextField();
    this.textPostCode = createIntegerOnlyTextField(5); // German postcodes
    this.textCity = createTextField();
    this.textGeoLatitude = createIntegerOrFloatOnlyTextField(20);
    this.textGeoLongitude = createIntegerOrFloatOnlyTextField(20);
    this.textSearchRadius = createIntegerOrFloatOnlyTextField(5);

    this.btnGeoRequest = this.createGeoRequestButton();
    this.btnGeoRequestController = new BtnGeoRequestController();

    this.initView();
    this.populateFields();
  }

  private void initView() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setAlignmentX(Component.LEFT_ALIGNMENT);
    setOpaque(true);
    setBorder(createTitledBorder(l10n.get("borderTitle.YourLocation")));
    setPreferredSize(totalDimension);
    setMaximumSize(totalDimension);
    setMinimumSize(totalDimension);

    add(createAddressFieldsPanel());
    add(Box.createRigidArea(new Dimension(0, 5)));
    add(createSeparator());
    add(createGeoFieldsPanel());
    add(Box.createRigidArea(new Dimension(0, 5)));
    add(btnGeoRequest);
    addPoweredByGooglePanelIfGoogleIsProvider();
    add(Box.createRigidArea(new Dimension(0, 5)));
    add(createSeparator());
    add(createDistancePanel());

    initEventListeners();
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
    btnRequestGeo.setMinimumSize(new Dimension(totalDimension.width - 10, 25));
    btnRequestGeo.setMaximumSize(new Dimension(totalDimension.width - 10, 25));
    btnRequestGeo.setPreferredSize(new Dimension(totalDimension.width - 10, 25));
    btnRequestGeo.setForeground(CustomColor.BUTTON_FOREGROUND);
    btnRequestGeo.setFocusable(false);
    btnRequestGeo.addActionListener(new BtnGeoRequestListener());

    return btnRequestGeo;
  }

  private void addPoweredByGooglePanelIfGoogleIsProvider() {
    // Evaluate current runtime class for interface GeocodingService
    Class<?> geocodingProvider = ((GeocodingWorker) workerService.getWorker())
        .getGeocodingService()
        .getClass();

    // No need to insert logo if provider is not Google
    if (geocodingProvider != GoogleGeocodingClient.class) return;

    // Google is current geocoding provider - insert copyright logo
    JPanel panel = new PoweredByGooglePanel();
    panel.setAlignmentX(LEFT_ALIGNMENT);
    panel.setMinimumSize(new Dimension(totalDimension.width, 17));
    panel.setMaximumSize(new Dimension(totalDimension.width, 17));
    panel.setPreferredSize(new Dimension(totalDimension.width, 17));

    add(Box.createRigidArea(new Dimension(0, 5)));
    add(panel);
  }

  private JPanel createGridPanel(int rows) {
    JPanel panel = new JPanel(new GridLayout(rows, 2));
    panel.setAlignmentX(LEFT_ALIGNMENT);
    panel.setPreferredSize(new Dimension(totalDimension.width, rows * 25 + 4 * rows));
    panel.setMinimumSize(new Dimension(totalDimension.width, rows * 25 + 4 * rows));
    panel.setMaximumSize(new Dimension(totalDimension.width, rows * 25 + 4 * rows));

    return panel;
  }

  private JSeparator createSeparator() {
    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
    separator.setAlignmentX(LEFT_ALIGNMENT);
    separator.setMaximumSize(new Dimension(totalDimension.width, 13));

    return separator;
  }

  private void initEventListeners() {
    textStreet.getDocument().addDocumentListener(btnGeoRequestController);
    textStreet.addFocusListener(this);
    textHouseNumber.addFocusListener(this);
    textCity.getDocument().addDocumentListener(btnGeoRequestController);
    textCity.addFocusListener(this);
    textPostCode.getDocument().addDocumentListener(btnGeoRequestController);
    textPostCode.addFocusListener(this);

    textSearchRadius.addFocusListener(this);
    textGeoLatitude.addFocusListener(this);
    textGeoLongitude.addFocusListener(this);

    // Fire event to BtnGeoRequestController, will initialize button state for geo request button.
    textStreet.setText(" ");
    textStreet.setText("");
  }

  private void populateFields() {
    userPrefs.readAddress().ifPresent(adr -> {
      textStreet.setText(adr.getStreet());
      textHouseNumber.setText(adr.getHouseNumber());
      textPostCode.setText(adr.getPostCode());
      textCity.setText(adr.getCity());
    });

    userPrefs.readGeo().ifPresent(geo -> {
      textSearchRadius.setText(String.valueOf(geo.getDistance().orElse(DEFAULT_SEARCH_RADIUS)));
      textGeoLatitude.setText(String.valueOf(geo.getLatitude()));
      textGeoLongitude.setText(String.valueOf(geo.getLongitude()));
    });
  }

  private void writeAddressToUserPrefs() {
    userPrefs.writeAddress(createAddressFromFields());
  }

  private Address createAddressFromFields() {
    if (!isValidUserAddress()) {
      LOGGER.warning(l10n.get("msg.IncompleteUserAddress"));
    }

    return new Address(
        "",
        textStreet.getText(),
        textHouseNumber.getText(),
        textCity.getText(),
        textPostCode.getText(),
        createGeoFromFields());
  }

  private boolean isValidUserAddress() {
    return textStreet.getDocument().getLength() > 0
        && textCity.getDocument().getLength() >= 2
        && textPostCode.getDocument().getLength() >= 4;
  }

  private Geo createGeoFromFields() {
    Geo geo = null;
    double lat;
    double lng;
    double searchRadius;

    if (!textGeoLatitude.getText().isEmpty() && !textGeoLongitude.getText().isEmpty()) {
      try {
        lat = Double.parseDouble(textGeoLatitude.getText());
        lng = Double.parseDouble(textGeoLongitude.getText());
        searchRadius = Double.parseDouble(textSearchRadius.getText());
        geo = new Geo(lat, lng, searchRadius);
      }
      catch (NumberFormatException e) {
        textGeoLatitude.setText("");
        textGeoLongitude.setText("");
        textSearchRadius.setText(String.valueOf(DEFAULT_SEARCH_RADIUS));
        Toolkit.getDefaultToolkit().beep();
      }
    }

    return geo;
  }

  // region Listeners/Callbacks
  // =====================================================================

  @Override
  public void focusGained(FocusEvent e) {
    // No need.
  }

  @Override
  public void focusLost(FocusEvent e) {
    writeAddressToUserPrefs();
  }

  /**
   * Controls the "enabled" state of button {@link #btnGeoRequest} depending on address fields
   * validation.
   */
  private class BtnGeoRequestController implements DocumentListener {
    @Override
    public void changedUpdate(DocumentEvent e) {
      changeGeoRequestButtonState();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
      changeGeoRequestButtonState();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
      changeGeoRequestButtonState();
    }

    private void changeGeoRequestButtonState() {
      btnGeoRequest.setEnabled(isValidUserAddress());
    }
  }

  /**
   * Click listener for button {@link #btnGeoRequest}.
   */
  private class BtnGeoRequestListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      // Force focusLost for eventually focussed field.
      requestFocusInWindow();

      ((GeocodingWorker) workerService.getWorker()).setUserAddress(createAddressFromFields());
      workerService.startOneShot(this::onGeocodingWorkerFinished);
      this.onGeocodingWorkerStarted();
    }

    private void onGeocodingWorkerStarted() {
      SwingUtilities.invokeLater(() -> {
        btnGeoRequest.setEnabled(false);
        footerController.onOneShotWorkerStarted(l10n.get("msg.GeocodingRequestRunning"));
      });
    }

    private void onGeocodingWorkerFinished(Geo data) {
      SwingUtilities.invokeLater(() -> {
        btnGeoRequest.setEnabled(true);
        footerController.onOneShotWorkerFinished();
        if (data == null) return;

        textGeoLatitude.setText(String.valueOf(data.getLatitude()));
        textGeoLongitude.setText(String.valueOf(data.getLongitude()));

        writeAddressToUserPrefs();
      });
    }
  }

  // endregion
}
