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
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

@Controller
public class PetrolTypeController {

  private static final Logger LOGGER = Logger.getLogger(PetrolTypeController.class.getName());

  private final PetrolTypePanel petrolTypePanel;
  private final ButtonGroup petrolTypeBtnGroup;
  private final UserPrefs userPrefs;

  @Autowired
  public PetrolTypeController(PetrolTypePanel petrolTypePanel, UserPrefs userPrefs) {
    this.petrolTypePanel = petrolTypePanel;
    this.userPrefs = userPrefs;
    this.petrolTypeBtnGroup = new ButtonGroup();
  }

  @PostConstruct
  private void init() {
    createPetrolTypeSelectionButtons();
    readSelectionFromUserPrefs();
  }

  private void createPetrolTypeSelectionButtons() {
    PetrolType[] petrolTypes = PetrolType.values();

    for (PetrolType petrolType : petrolTypes) {
      JRadioButton button = new JRadioButton(petrolType.getReadableName());
      button.setForeground(CustomColor.BUTTON_FOREGROUND);
      button.setFocusable(false);

      petrolTypeBtnGroup.add(button);
      petrolTypePanel.getPetrolTypeSelectionPanel().add(button);

      addPetrolTypeSelectionListener(button, petrolType);
    }
  }

  private void addPetrolTypeSelectionListener(JRadioButton button, PetrolType petrolType) {
    button.setActionCommand(petrolType.name());

    button.addActionListener(e -> {
      JRadioButton selectedPetrolTypeButton = (JRadioButton) e.getSource();
      writeSelectionToUserPrefs(selectedPetrolTypeButton.getActionCommand());
    });
  }

  private void readSelectionFromUserPrefs() {
    List<AbstractButton> allRadioButtons = Collections
        .list(petrolTypeBtnGroup.getElements());

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
}
