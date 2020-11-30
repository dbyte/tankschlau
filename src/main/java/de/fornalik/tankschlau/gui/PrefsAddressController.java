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

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.logging.Logger;

@Controller
class PrefsAddressController {

  private static final Logger LOGGER = Logger.getLogger(PrefsAddressController.class.getName());
  private static final double DEFAULT_SEARCH_RADIUS = 5.0;

  private final PrefsAddressPanel view;
  private final AddressFieldsDocumentListener addressFieldsDocumentListener;
  private final AddressFieldsFocusListener addressFieldsFocusListener;
  private final FooterController footerController;
  private final WorkerService<Geo> workerService;
  private final UserPrefs userPrefs;
  private final Localization l10n;

  @Autowired
  PrefsAddressController(
      PrefsAddressPanel view,
      FooterController footerController,
      WorkerService<Geo> geocodingWorkerService,
      UserPrefs userPrefs,
      Localization l10n) {

    this.view = view;
    this.footerController = footerController;
    this.workerService = geocodingWorkerService;
    this.userPrefs = userPrefs;
    this.l10n = l10n;

    this.addressFieldsDocumentListener = new AddressFieldsDocumentListener();
    this.addressFieldsFocusListener = new AddressFieldsFocusListener();
  }

  @PostConstruct
  void init() {
    addPoweredByGoogleLogoIfGoogleIsProvider();
    registerActionListeners();
    registerFocusListener();
    populateFields();
  }

  private void addPoweredByGoogleLogoIfGoogleIsProvider() {
    // Evaluate current runtime class for interface GeocodingService
    Class<?> geocodingProvider = ((GeocodingWorker) workerService.getWorker())
        .getGeocodingService()
        .getClass();

    // No need to insert logo if provider is not Google
    if (geocodingProvider != GoogleGeocodingClient.class) return;

    view.insertPoweredByGooglePanel();
  }

  private void registerActionListeners() {
    view.getBtnGeoRequest().addActionListener(new BtnGeoRequestListener());

    view.getTextStreet().getDocument().addDocumentListener(addressFieldsDocumentListener);
    view.getTextStreet().addFocusListener(addressFieldsFocusListener);
    view.getTextHouseNumber().addFocusListener(addressFieldsFocusListener);
    view.getTextCity().getDocument().addDocumentListener(addressFieldsDocumentListener);
    view.getTextCity().addFocusListener(addressFieldsFocusListener);
    view.getTextPostCode().getDocument().addDocumentListener(addressFieldsDocumentListener);
    view.getTextPostCode().addFocusListener(addressFieldsFocusListener);

    // Init button state.
    changeGeoRequestButtonState();
  }

  private void registerFocusListener() {
    view.addFocusListener(new FocusListener() {
      @Override
      public void focusGained(FocusEvent e) {
        // No need.
      }

      @Override
      public void focusLost(FocusEvent e) {
        writeAddressToUserPrefs();
      }
    });
  }

  private void populateFields() {
    userPrefs.readAddress().ifPresent(adr -> {
      view.getTextStreet().setText(adr.getStreet());
      view.getTextHouseNumber().setText(adr.getHouseNumber());
      view.getTextPostCode().setText(adr.getPostCode());
      view.getTextCity().setText(adr.getCity());
    });

    userPrefs.readGeo().ifPresent(geo -> {
      view.getTextSearchRadius()
          .setText(String.valueOf(geo.getDistance().orElse(DEFAULT_SEARCH_RADIUS)));
      view.getTextGeoLatitude().setText(String.valueOf(geo.getLatitude()));
      view.getTextGeoLongitude().setText(String.valueOf(geo.getLongitude()));
    });
  }

  private void writeAddressToUserPrefs() {
    userPrefs.writeAddress(createAddressFromFields());
  }

  private boolean isValidUserAddress() {
    return view.getTextStreet().getDocument().getLength() > 0
        && view.getTextCity().getDocument().getLength() >= 2
        && view.getTextPostCode().getDocument().getLength() >= 4;
  }

  private void changeGeoRequestButtonState() {
    view.getBtnGeoRequest().setEnabled(isValidUserAddress());
  }

  private Address createAddressFromFields() {
    if (!isValidUserAddress()) {
      LOGGER.warning(l10n.get("msg.IncompleteUserAddress"));
    }

    return new Address(
        "",
        view.getTextStreet().getText(),
        view.getTextHouseNumber().getText(),
        view.getTextCity().getText(),
        view.getTextPostCode().getText(),
        createGeoFromFields());
  }

  private Geo createGeoFromFields() {
    Geo geo = null;
    double lat;
    double lng;
    double searchRadius;

    if (!view.getTextGeoLatitude().getText().isEmpty() && !view
        .getTextGeoLongitude().getText().isEmpty()) {
      try {
        lat = Double.parseDouble(view.getTextGeoLatitude().getText());
        lng = Double.parseDouble(view.getTextGeoLongitude().getText());
        searchRadius = Double.parseDouble(view.getTextSearchRadius().getText());
        geo = new Geo(lat, lng, searchRadius);
      }
      catch (NumberFormatException e) {
        view.getTextGeoLatitude().setText("");
        view.getTextGeoLongitude().setText("");
        view.getTextSearchRadius().setText(String.valueOf(DEFAULT_SEARCH_RADIUS));
        Toolkit.getDefaultToolkit().beep();
      }
    }

    return geo;
  }

  /**
   * Controls what to do when an address field has lost its focus.
   */
  private class AddressFieldsFocusListener implements FocusListener {
    @Override
    public void focusGained(FocusEvent e) {
      // No need.
    }

    @Override
    public void focusLost(FocusEvent e) {
      writeAddressToUserPrefs();
    }
  }

  /**
   * Controls the "enabled" state of Geo Request button depending on address fields validation.
   */
  private class AddressFieldsDocumentListener implements DocumentListener {
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
  }

  /**
   * Click listener for Geo Request button.
   */
  private class BtnGeoRequestListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
      // Force focusLost for eventually focussed field.
      view.requestFocusInWindow();

      // Do business stuff.
      ((GeocodingWorker) workerService.getWorker()).setUserAddress(createAddressFromFields());
      workerService.startOneShot(this::onGeocodingWorkerFinished);
      this.onGeocodingWorkerStarted();
    }

    private void onGeocodingWorkerStarted() {
      SwingUtilities.invokeLater(() -> {
        view.getBtnGeoRequest().setEnabled(false);
        footerController.onOneShotWorkerStarted(l10n.get("msg.GeocodingRequestRunning"));
      });
    }

    private void onGeocodingWorkerFinished(Geo data) {
      SwingUtilities.invokeLater(() -> {
        view.getBtnGeoRequest().setEnabled(true);
        footerController.onOneShotWorkerFinished();
        if (data == null) return;

        view.getTextGeoLatitude().setText(String.valueOf(data.getLatitude()));
        view.getTextGeoLongitude().setText(String.valueOf(data.getLongitude()));

        userPrefs.writeGeo(new Geo(data.getLatitude(), data.getLongitude()));
      });
    }
  }
}
