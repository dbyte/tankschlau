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
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * User preferences panel for cyclic background services.
 */
@Component
class PrefsCyclePanel extends JPanel implements PrefsFactoryMixin {

  private static final int DEFAULT_ROW_HEIGHT = 25;
  private static final Dimension DEFAULT_SIZE = new Dimension(300, 130);

  private final JTextField textCycleRate;
  private final JTextField textMessageDelayWithNumberOfCalls;
  private final JCheckBox checkEnableMessages;
  private final GridBagConstraints constraints;

  private final Localization l10n;

  @Autowired
  PrefsCyclePanel(Localization l10n) {
    this.l10n = l10n;

    this.textCycleRate = createIntegerOnlyTextField(5);
    this.textMessageDelayWithNumberOfCalls = createIntegerOnlyTextField(3);
    this.checkEnableMessages = createEnableMessagesCheckbox(l10n.get("label.EnableMessaging"));

    this.constraints = new GridBagConstraints();
  }

  @PostConstruct
  private void initView() {
    setLayout(new GridBagLayout());
    setOpaque(true);
    setBorder(createTitledBorder(l10n.get("borderTitle.AutomaticDataUpdate")));
    setMaximumSize(DEFAULT_SIZE);

    constraints.anchor = GridBagConstraints.WEST;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(0, 5, 0, 5);
    createCycleRatePref();

    // ---> More rows go here <---
  }

  private void createCycleRatePref() {
    constraints.gridy = 0; // Row 1 ---------------------------------------

    constraints.gridx = 0;
    JLabel labelCycleEvery = createLabel(l10n.get("label.CycleEvery"), SwingConstants.LEFT);
    addToPanel(labelCycleEvery, 120, constraints);

    constraints.gridx = 1;
    addToPanel(textCycleRate, 60, constraints);

    constraints.gridx = 2;
    addToPanel(createLabel(l10n.get("label.Seconds"), SwingConstants.LEFT), 100, constraints);

    constraints.gridy = 1; // Row 2 ---------------------------------------
    constraints.gridx = 0;
    constraints.gridwidth = 3;
    addToPanel(new JSeparator(), DEFAULT_SIZE.width, constraints);
    constraints.gridwidth = 1;

    constraints.gridy = 2; // Row 3 ---------------------------------------
    constraints.gridx = 0;
    constraints.gridwidth = 3;
    addToPanel(checkEnableMessages, 120, constraints);
    constraints.gridwidth = 1;

    constraints.gridy = 3; // Row 4 ---------------------------------------

    constraints.gridx = 0;
    JLabel labelMessageMaxCallsUntilForceSend = createLabel(
        l10n.get("label.CycleMessageDelayWithNumberOfCalls"),
        SwingConstants.LEFT);
    addToPanel(labelMessageMaxCallsUntilForceSend, 180, constraints);

    constraints.gridx = 1;
    addToPanel(textMessageDelayWithNumberOfCalls, 60, constraints);

    constraints.gridx = 2;
    addToPanel(createLabel(l10n.get("label.Updates"), SwingConstants.LEFT), 120, constraints);
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

  JTextField getTextCycleRate() {
    return textCycleRate;
  }

  JTextField getTextMessageDelayWithNumberOfCalls() {
    return textMessageDelayWithNumberOfCalls;
  }

  JCheckBox getCheckEnableMessages() {
    return checkEnableMessages;
  }
}
