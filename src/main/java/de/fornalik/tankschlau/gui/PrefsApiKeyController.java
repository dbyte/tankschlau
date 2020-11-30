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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

@Controller
class PrefsApiKeyController {

  private final PrefsApiKeyModel model;
  private final PrefsApiKeyPanel view;
  private final ApiKeyFieldsFocusListener focusListener;

  @Autowired
  PrefsApiKeyController(PrefsApiKeyModel model, PrefsApiKeyPanel view) {
    this.model = model;
    this.view = view;
    this.focusListener = new ApiKeyFieldsFocusListener();
  }

  @PostConstruct
  private void init() {
    registerFocusListeners();
    populateFields();
  }

  private void registerFocusListeners() {
    view.getTextPetrolStationsServiceApiKey().addFocusListener(focusListener);
    view.getTextGeocodingServiceApiKey().addFocusListener(focusListener);
    view.getTextMessageServiceApiKey().addFocusListener(focusListener);
    view.getTextMessageServiceUserKey().addFocusListener(focusListener);
  }

  private void populateFields() {
    view.getTextPetrolStationsServiceApiKey().setText(model.readPetrolStationsApiKey());
    view.getTextGeocodingServiceApiKey().setText(model.readGeocodingApiKey());
    view.getTextMessageServiceApiKey().setText(model.readPushmessageApiKey());
    view.getTextMessageServiceUserKey().setText(model.readPushmessageUserId());
  }

  private String getValueFromPasswordField(JPasswordField field) {
    return String.valueOf(field.getPassword());
  }

  /**
   * Controls what to do when an API Key field has lost its focus.
   */
  private class ApiKeyFieldsFocusListener implements FocusListener {
    @Override
    public void focusGained(FocusEvent e) {
      // No need.
    }

    @Override
    public void focusLost(FocusEvent e) {
      if (!(e.getSource() instanceof JPasswordField)) return;
      JPasswordField field = (JPasswordField) e.getSource();

      if (field == view.getTextPetrolStationsServiceApiKey()) {
        model.writePetrolStationsApiKey(getValueFromPasswordField(
            view.getTextPetrolStationsServiceApiKey()));
      }

      else if (field == view.getTextGeocodingServiceApiKey()) {
        model.writeGeocodingApiKey(getValueFromPasswordField(
            view.getTextGeocodingServiceApiKey()));
      }

      else if (field == view.getTextMessageServiceApiKey()) {
        model.writePushmessageApiKey(getValueFromPasswordField(
            view.getTextMessageServiceApiKey()));
      }
      else if (field == view.getTextMessageServiceUserKey()) {
        model.writePushmessageUserId(getValueFromPasswordField(
            view.getTextMessageServiceUserKey()));
      }
    }
  }
}
