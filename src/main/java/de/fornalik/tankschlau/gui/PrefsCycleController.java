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
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.logging.Logger;

@Controller
public class PrefsCycleController {

  private static final Logger LOGGER = Logger.getLogger(PrefsCycleController.class.getName());
  private static final int MINIMUM_CYCLE_RATE = 5;
  private static final int DEFAULT_CYCLE_RATE = 300;

  private final PrefsCycleModel model;
  private final PrefsCycleView view;

  @Autowired
  public PrefsCycleController(PrefsCycleModel model, PrefsCycleView view) {
    this.model = model;
    this.view = view;
  }

  @PostConstruct
  private void init() {
    registerFocusListeners();
    populateFields();
  }

  private void registerFocusListeners() {
    CycleFieldsFocusListener cycleFieldsFocusListener = new CycleFieldsFocusListener();
    ItemListener checkboxListener = new CheckboxListener();

    view.getTextCycleRate().addFocusListener(cycleFieldsFocusListener);
    view.getTextMessageDelayWithNumberOfCalls().addFocusListener(cycleFieldsFocusListener);
    view.getCheckEnableMessages().addItemListener(checkboxListener);
  }

  private void populateFields() {
    view.getTextCycleRate()
        .setText(String.valueOf(model.readUserPrefsPetrolStationsUpdateCycleRate()));

    view.getCheckEnableMessages().setSelected(model.readUserPrefsPushMessageEnabled());

    view.getTextMessageDelayWithNumberOfCalls()
        .setText(String.valueOf(model.readUserPrefsPushMessageDelayWithNumberOfCalls()));

    view.getTextMessageDelayWithNumberOfCalls().setEnabled(model.readUserPrefsPushMessageEnabled());
  }

  private class CycleFieldsFocusListener implements FocusListener {
    @Override
    public void focusGained(FocusEvent e) {
      // No need.
    }

    @Override
    public void focusLost(FocusEvent e) {
      if (!(e.getSource() instanceof JTextField))
        return;

      if (e.getSource() == view.getTextCycleRate()) {
        int value = legalizeTextFieldToInteger(e, MINIMUM_CYCLE_RATE, DEFAULT_CYCLE_RATE);
        model.writeUserPrefsPetrolStationsUpdateCycleRate(value);
      }

      else if (e.getSource() == view.getTextMessageDelayWithNumberOfCalls()) {
        int value = legalizeTextFieldToInteger(e, 0, 20);
        model.writeUserPrefsPushMessageDelayWithNumberOfCalls(value);
      }
    }

    private int legalizeTextFieldToInteger(FocusEvent e, int minimumValue, int fallbackValue) {
      if (!(e.getSource() instanceof JTextField))
        return fallbackValue;

      JTextField textField = (JTextField) e.getSource();
      if (textField.getDocument() == null)
        return fallbackValue;

      int length = textField.getDocument().getLength();
      if (length == 0) {
        textField.setText(String.valueOf(fallbackValue));
        return fallbackValue;
      }

      int out = 0;

      try {
        String text = textField.getDocument().getText(0, length);
        out = Integer.parseInt(text);
      }
      catch (BadLocationException ex) {
        LOGGER.severe(ex.getMessage());
      }

      if (out < minimumValue) {
        Toolkit.getDefaultToolkit().beep();
        textField.setText(String.valueOf(fallbackValue));
        return fallbackValue;
      }

      return out;
    }
  }

  private class CheckboxListener implements ItemListener {
    @Override
    public void itemStateChanged(ItemEvent e) {
      if (e.getSource() == view.getCheckEnableMessages()) {
        boolean isChecked = e.getStateChange() == ItemEvent.SELECTED;

        model.writeUserPrefsPushMessageEnabled(isChecked);
        view.getTextMessageDelayWithNumberOfCalls().setEnabled(isChecked);
      }
    }
  }
}
