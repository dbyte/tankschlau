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
import de.fornalik.tankschlau.util.Localization;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

/**
 * User preferences panel for cyclic background services.
 */
class PrefsCyclePanel extends JPanel implements PrefsFactoryMixin {

  private static final Localization L10N = Localization.getInstance();
  private static final int MINIMUM_CYCLE_RATE = 5;
  private static final int DEFAULT_CYCLE_RATE = 300;
  private static final int DEFAULT_ROW_HEIGHT = 25;
  private static final Dimension TOTAL_DIMENSION = new Dimension(300, 130);

  private final JTextField textCycleRate;
  private final JTextField textMessageDelayWithNumberOfCalls;
  private final JCheckBox checkEnableMessages;
  private final GridBagConstraints constraints;

  private final UserPrefs userPrefs;
  private final CycleFocusListener focusListener;

  PrefsCyclePanel(UserPrefs userPrefs) {
    this.userPrefs = userPrefs;

    this.textCycleRate = createIntegerOnlyTextField(5);
    this.textMessageDelayWithNumberOfCalls = createIntegerOnlyTextField(3);
    this.checkEnableMessages = createEnableMessagesCheckbox(L10N.get("label.EnableMessaging"));

    this.constraints = new GridBagConstraints();
    this.focusListener = new CycleFocusListener();

    this.initView();
  }

  private void initView() {
    setLayout(new GridBagLayout());
    setOpaque(true);
    setBorder(createTitledBorder(L10N.get("borderTitle.AutomaticDataUpdate")));
    setMaximumSize(TOTAL_DIMENSION);

    constraints.anchor = GridBagConstraints.WEST;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(0, 5, 0, 5);
    createCycleRatePref();

    // ---> More rows go here <---
  }

  private void createCycleRatePref() {
    constraints.gridy = 0; // Row 1 ---------------------------------------

    constraints.gridx = 0;
    JLabel labelCycleEvery = createLabel(L10N.get("label.CycleEvery"), SwingConstants.LEFT);
    addToPanel(labelCycleEvery, 120, constraints);

    constraints.gridx = 1;
    textCycleRate.setText(String.valueOf(userPrefs.readPetrolStationsUpdateCycleRate()));
    textCycleRate.addFocusListener(focusListener);
    addToPanel(textCycleRate, 60, constraints);

    constraints.gridx = 2;
    addToPanel(createLabel(L10N.get("label.Seconds"), SwingConstants.LEFT), 100, constraints);

    constraints.gridy = 1; // Row 2 ---------------------------------------
    constraints.gridx = 0;
    constraints.gridwidth = 3;
    addToPanel(new JSeparator(), TOTAL_DIMENSION.width, constraints);
    constraints.gridwidth = 1;

    constraints.gridy = 2; // Row 3 ---------------------------------------
    constraints.gridx = 0;
    constraints.gridwidth = 3;
    checkEnableMessages.setSelected(userPrefs.readPushMessageEnabled());
    checkEnableMessages.addItemListener(new CheckboxListener());
    addToPanel(checkEnableMessages, 120, constraints);
    constraints.gridwidth = 1;

    constraints.gridy = 3; // Row 4 ---------------------------------------

    constraints.gridx = 0;
    JLabel labelMessageMaxCallsUntilForceSend = createLabel(
        L10N.get("label.CycleMessageDelayWithNumberOfCalls"),
        SwingConstants.LEFT);
    addToPanel(labelMessageMaxCallsUntilForceSend, 180, constraints);

    constraints.gridx = 1;
    textMessageDelayWithNumberOfCalls
        .setText(String.valueOf(userPrefs.readPushMessageDelayWithNumberOfCalls()));
    textMessageDelayWithNumberOfCalls.setEnabled(userPrefs.readPushMessageEnabled());
    textMessageDelayWithNumberOfCalls.addFocusListener(focusListener);
    addToPanel(textMessageDelayWithNumberOfCalls, 60, constraints);

    constraints.gridx = 2;
    addToPanel(createLabel(L10N.get("label.Updates"), SwingConstants.LEFT), 120, constraints);
  }

  private void addToPanel(
      JComponent component,
      int width,
      GridBagConstraints constraints) {

    this.setSize(component, width);
    this.add(component, constraints);
  }

  private void setSize(JComponent component, int width) {
    component.setMinimumSize(new Dimension(width, DEFAULT_ROW_HEIGHT));
    component.setMaximumSize(new Dimension(width, DEFAULT_ROW_HEIGHT));
    component.setPreferredSize(new Dimension(width, DEFAULT_ROW_HEIGHT));
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
      ex.printStackTrace();
    }

    if (out < minimumValue) {
      Toolkit.getDefaultToolkit().beep();
      textField.setText(String.valueOf(fallbackValue));
      return fallbackValue;
    }

    return out;
  }

  // region Listeners
  // =====================================================================

  private class CycleFocusListener implements FocusListener {
    @Override
    public void focusGained(FocusEvent e) {
      // No need.
    }

    @Override
    public void focusLost(FocusEvent e) {
      if (!(e.getSource() instanceof JTextField))
        return;

      if (e.getSource() == textCycleRate) {
        int value = legalizeTextFieldToInteger(e, MINIMUM_CYCLE_RATE, DEFAULT_CYCLE_RATE);
        userPrefs.writePetrolStationsUpdateCycleRate(value);
      }

      else if (e.getSource() == textMessageDelayWithNumberOfCalls) {
        int value = legalizeTextFieldToInteger(e, 1, 20);
        userPrefs.writePushMessageDelayWithNumberOfCalls(value);
      }
    }
  }

  private class CheckboxListener implements ItemListener {
    @Override
    public void itemStateChanged(ItemEvent e) {
      if (e.getSource() == checkEnableMessages) {
        boolean isChecked = e.getStateChange() == ItemEvent.SELECTED;

        userPrefs.writePushMessageEnabled(isChecked);
        textMessageDelayWithNumberOfCalls.setEnabled(isChecked);
      }
    }
  }

  // endregion
}