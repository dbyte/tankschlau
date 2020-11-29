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

import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.user.UserPrefs;
import de.fornalik.tankschlau.util.Localization;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

/**
 * Chooser for preferred PetrolType.
 */
@Controller
class PetrolTypePanel extends JPanel implements ActionListener {

  private static final Logger LOGGER = Logger.getLogger(PetrolTypePanel.class.getName());

  private final UserPrefs userPrefs;
  private final Localization l10n;
  private final ButtonGroup petrolTypeBtnGroup;

  @Autowired
  PetrolTypePanel(UserPrefs userPrefs, Localization l10n) {
    this.userPrefs = userPrefs;
    this.l10n = l10n;
    this.petrolTypeBtnGroup = new ButtonGroup();

    this.initView();
    this.readSelectionFromUserPrefs();
  }

  private void initView() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    add(createHeaderLabel());
    add(createRadioPanel());
  }

  private JPanel createHeaderLabel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel label = new JLabel(l10n.get("label.PreferredPetrolType"));
    label.setForeground(CustomColor.LABEL_TEXT);

    panel.add(label);
    return panel;
  }

  private JPanel createRadioPanel() {
    JPanel panel = new JPanel(new GridLayout(0, 1));
    PetrolType[] petrolTypes = PetrolType.values();

    for (PetrolType petrolType : petrolTypes) {
      JRadioButton radioButton = new JRadioButton(petrolType.getReadableName());
      radioButton.setForeground(CustomColor.BUTTON_FOREGROUND);
      radioButton.addActionListener(this);
      radioButton.setActionCommand(petrolType.name());
      radioButton.setFocusable(false);
      petrolTypeBtnGroup.add(radioButton);
      panel.add(radioButton);
    }

    return panel;
  }

  private void readSelectionFromUserPrefs() {
    List<AbstractButton> allRadioButtons = Collections.list(petrolTypeBtnGroup.getElements());

    for (AbstractButton radioButton : allRadioButtons) {
      if (userPrefs.readPreferredPetrolType().name().equals(radioButton.getActionCommand())) {
        radioButton.setSelected(true);
        break;
      }
    }
  }

  private void writeSelectionToUserPrefs(String petrolTypeString) {
    if (petrolTypeString == null || petrolTypeString.isEmpty()) {
      String errMsg = "Illegal petrol type selection: Empty or null string.";
      LOGGER.severe(errMsg);
      throw new IllegalStateException(errMsg);
    }

    if (!EnumUtils.isValidEnum(PetrolType.class, petrolTypeString)) {
      String errMsg = "Illegal petrol type selection: " + petrolTypeString;
      LOGGER.severe(errMsg);
      throw new IllegalStateException(errMsg);
    }

    userPrefs.writePreferredPetrolType(PetrolType.valueOf(petrolTypeString));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (!(e.getSource() instanceof JRadioButton)) return;

    String enumString = petrolTypeBtnGroup.getSelection().getActionCommand();
    writeSelectionToUserPrefs(enumString);
  }
}
