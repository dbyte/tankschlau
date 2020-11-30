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
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.HashMap;
import java.util.Map;

@Controller
class PrefsAddressController {

  private static final double DEFAULT_SEARCH_RADIUS = 5.0;

  private final PrefsAddressView view;
  private final AddressFieldsDocumentListener addressFieldsDocumentListener;
  private final AddressFieldsFocusListener addressFieldsFocusListener;
  private final FooterController footerController;
  private final PrefsAddressModel model;
  private final Localization l10n;

  @Autowired
  PrefsAddressController(
      PrefsAddressModel model,
      PrefsAddressView view,
      FooterController footerController,
      Localization l10n) {

    this.view = view;
    this.model = model;
    this.footerController = footerController;
    this.l10n = l10n;

    this.addressFieldsDocumentListener = new AddressFieldsDocumentListener();
    this.addressFieldsFocusListener = new AddressFieldsFocusListener();
  }

  @PostConstruct
  void init() {
    addPoweredByGoogleLogoIfGoogleIsProvider();
    registerActionListeners();
    populateFields();
  }

  private void addPoweredByGoogleLogoIfGoogleIsProvider() {
    // Only need to insert logo if provider is Google Geocoding
    if (model.isGeoServiceGoogleGeocodingImplementation()) {
      view.insertPoweredByGooglePanel();
    }
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

  private void populateFields() {
    model.readAddressFromUserPrefs().ifPresent(adr -> {
      view.getTextStreet().setText(adr.getStreet());
      view.getTextHouseNumber().setText(adr.getHouseNumber());
      view.getTextPostCode().setText(adr.getPostCode());
      view.getTextCity().setText(adr.getCity());
    });

    model.readGeoFromUserPrefs().ifPresent(geo -> {
      view.getTextSearchRadius()
          .setText(String.valueOf(geo.getDistance().orElse(DEFAULT_SEARCH_RADIUS)));
      view.getTextGeoLatitude().setText(String.valueOf(geo.getLatitude()));
      view.getTextGeoLongitude().setText(String.valueOf(geo.getLongitude()));
    });
  }

  private void writeAddressToUserPrefs() {
    model.writeAddressToUserPrefs(convertAddressFieldsToMap());
  }

  private boolean isValidUserAddress() {
    return view.getTextStreet().getDocument().getLength() > 0
        && view.getTextCity().getDocument().getLength() >= 2
        && view.getTextPostCode().getDocument().getLength() >= 4;
  }

  private void changeGeoRequestButtonState() {
    view.getBtnGeoRequest().setEnabled(isValidUserAddress());
  }

  private Map<String, String> convertAddressFieldsToMap() {
    Map<String, String> map = new HashMap<>();

    map.put("street", view.getTextStreet().getText());
    map.put("houseNumber", view.getTextHouseNumber().getText());
    map.put("city", view.getTextCity().getText());
    map.put("postCode", view.getTextPostCode().getText());
    map.putAll(convertGeoFieldsToMap());

    return map;
  }

  private Map<String, String> convertGeoFieldsToMap() {
    Map<String, String> map = new HashMap<>();

    map.put("latitude", view.getTextGeoLatitude().getText());
    map.put("longitude", view.getTextGeoLongitude().getText());
    map.put("searchRadius", view.getTextSearchRadius().getText());

    return map;
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
      view.requestFocusInWindow(); // Force focusLost for eventually focussed field.
      model.queryGeoDataForAddress(convertAddressFieldsToMap(), this::onGeoQueryFinished);
      this.onGeoQueryStarted();
    }

    private void onGeoQueryStarted() {
      SwingUtilities.invokeLater(() -> {
        view.getBtnGeoRequest().setEnabled(false);
        footerController.onOneShotWorkerStarted(l10n.get("msg.GeocodingRequestRunning"));
      });
    }

    private void onGeoQueryFinished(Pair<Double, Double> latLonResult) {
      SwingUtilities.invokeLater(() -> {
        view.getBtnGeoRequest().setEnabled(true);
        footerController.onOneShotWorkerFinished();

        if (latLonResult == null) return;

        view.getTextGeoLatitude().setText(String.valueOf(latLonResult.getLeft()));
        view.getTextGeoLongitude().setText(String.valueOf(latLonResult.getRight()));
      });
    }
  }
}
