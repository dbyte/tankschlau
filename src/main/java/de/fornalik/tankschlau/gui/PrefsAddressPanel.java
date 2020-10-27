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
import de.fornalik.tankschlau.storage.GeocodingWorker;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.WorkerService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * User preferences panel for user location
 */
class PrefsAddressPanel extends JPanel implements FocusListener, PrefsFactoryMixin {

  private static final Localization L10N = Localization.getInstance();
  private static final Dimension totalDimension = new Dimension(350, 250);

  private static WorkerService<Geo> workerService;
  private final UserPrefs userPrefs;

  private final JTextField textStreet;
  private final JTextField textHouseNumber;
  private final JTextField textPostCode;
  private final JTextField textCity;
  private final JTextField textGeoLatitude;
  private final JTextField textGeoLongitude;
  private final JButton btnGeoRequest;

  private final BtnGeoRequestController btnGeoRequestController;
  private final FooterPanel footerPanel;

  PrefsAddressPanel(UserPrefs userPrefs, FooterPanel footerPanel) {
    super();

    workerService = new SwingWorkerService<>(new GeocodingWorker());
    this.userPrefs = userPrefs;
    this.footerPanel = footerPanel;

    this.textStreet = createTextField();
    this.textHouseNumber = createTextField();
    this.textPostCode = createTextField();
    this.textCity = createTextField();
    this.textGeoLatitude = createTextField();
    this.textGeoLongitude = createTextField();

    this.btnGeoRequest = this.createGeoRequestButton();
    this.btnGeoRequestController = new BtnGeoRequestController();

    this.initView();
    this.populateFields();
  }

  private void initView() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setAlignmentX(Component.LEFT_ALIGNMENT);
    setOpaque(true);
    setBorder(createTitledBorder(L10N.get("borderTitle.YourLocation")));
    setPreferredSize(totalDimension);
    setMaximumSize(totalDimension);
    setMinimumSize(totalDimension);

    add(createAddressFieldsPanel());
    add(createSeparator());
    add(createGeoFieldsPanel());
    add(Box.createRigidArea(new Dimension(0, 5)));
    add(btnGeoRequest);

    initEventListeners();
  }

  private JPanel createAddressFieldsPanel() {
    JPanel panel = createGridPanel(4);

    panel.add(createLabel(L10N.get("label.AdrStreet")));
    panel.add(textStreet);

    panel.add(createLabel(L10N.get("label.AdrHouseNumber")));
    panel.add(textHouseNumber);

    panel.add(createLabel(L10N.get("label.AdrPostcode")));
    panel.add(textPostCode);

    panel.add(createLabel(L10N.get("label.AdrCity")));
    panel.add(textCity);

    return panel;
  }

  private JPanel createGeoFieldsPanel() {
    JPanel panel = createGridPanel(2);

    panel.add(createLabel(L10N.get("label.Latitude")));
    panel.add(textGeoLatitude);

    panel.add(createLabel(L10N.get("label.Longitude")));
    panel.add(textGeoLongitude);

    return panel;
  }

  private JButton createGeoRequestButton() {
    JButton btnRequestGeo = new JButton(L10N.get("button.EvaluateLatLonByAddress"));
    btnRequestGeo.setAlignmentX(LEFT_ALIGNMENT);
    btnRequestGeo.setMinimumSize(new Dimension(totalDimension.width - 10, 30));
    btnRequestGeo.setMaximumSize(new Dimension(totalDimension.width - 10, 30));
    btnRequestGeo.setPreferredSize(new Dimension(totalDimension.width - 10, 30));
    btnRequestGeo.setForeground(CustomColor.BUTTON_FOREGROUND);
    btnRequestGeo.setFocusable(false);
    btnRequestGeo.addActionListener(new BtnGeoRequestListener());

    return btnRequestGeo;
  }

  private JPanel createGridPanel(int rowCount) {
    JPanel panel = new JPanel(new GridLayout(rowCount, 2));
    panel.setAlignmentX(LEFT_ALIGNMENT);
    panel.setPreferredSize(new Dimension(totalDimension.width, rowCount * 25 + 4 * rowCount));
    panel.setMinimumSize(new Dimension(totalDimension.width, rowCount * 25 + 4 * rowCount));
    panel.setMaximumSize(new Dimension(totalDimension.width, rowCount * 25 + 4 * rowCount));

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
    textGeoLatitude.addFocusListener(this);
    textGeoLongitude.addFocusListener(this);

    // Fire event to BtnGeoRequestController, will initialize button state.
    textStreet.setText(" ");
    textStreet.setText("");
  }

  private void populateFields() {
    userPrefs.readAddress().ifPresent(adr -> {
      textStreet.setText(adr.getStreet());
      textHouseNumber.setText(adr.getHouseNumber());
      textPostCode.setText(adr.getPostCode());
      textCity.setText(adr.getCity());

      adr.getGeo().ifPresent(geo -> {
        textGeoLatitude.setText(String.valueOf(geo.getLatitude()));
        textGeoLongitude.setText(String.valueOf(geo.getLongitude()));
      });
    });
  }

  private void writeAddress() {
    Address address = new Address(
        "",
        textStreet.getText(),
        textHouseNumber.getText(),
        textCity.getText(),
        textPostCode.getText(),
        createGeoFromFields());

    userPrefs.writeAddress(address);
  }

  private Geo createGeoFromFields() {
    Geo geo = null;
    double lat, lng;

    if (!textGeoLatitude.getText().isEmpty() && !textGeoLongitude.getText().isEmpty()) {
      try {
        lat = Double.parseDouble(textGeoLatitude.getText());
        lng = Double.parseDouble(textGeoLongitude.getText());
        geo = new Geo(lat, lng);
      }
      catch (NumberFormatException e) {
        textGeoLatitude.setText("");
        textGeoLongitude.setText("");
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
    writeAddress();
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

    public void changeGeoRequestButtonState() {
      boolean enabled = textStreet.getDocument().getLength() > 0
          && textCity.getDocument().getLength() >= 2
          && textPostCode.getDocument().getLength() >= 4;

      btnGeoRequest.setEnabled(enabled);
    }
  }

  /**
   * Click listener for button {@link #btnGeoRequest}.
   */
  private class BtnGeoRequestListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      workerService.startOneShot(this::onOneShotWorkerFinished);
      btnGeoRequest.setEnabled(false);
      footerPanel.onOneShotWorkerStarted("Geocoding");
    }

    private void onOneShotWorkerFinished(Geo data) {
      SwingUtilities.invokeLater(() -> {
        btnGeoRequest.setEnabled(true);
        footerPanel.onOneShotWorkerFinished();
        textGeoLatitude.setText(String.valueOf(data.getLatitude()));
        textGeoLongitude.setText(String.valueOf(data.getLongitude()));
      });
    }
  }

  // endregion
}
