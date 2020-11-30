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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.util.Collections;
import java.util.List;

@Controller
public class PetrolTypeController {

  private final PetrolTypeModel model;
  private final PetrolTypeView view;
  private final ButtonGroup petrolTypeBtnGroup;

  @Autowired
  public PetrolTypeController(PetrolTypeModel model, PetrolTypeView view) {
    this.model = model;
    this.view = view;
    this.petrolTypeBtnGroup = new ButtonGroup();
  }

  @PostConstruct
  private void init() {
    createPetrolTypeSelectionButtons();
    readSelectionFromUserPrefs();
  }

  private void createPetrolTypeSelectionButtons() {
    for (PetrolType petrolType : PetrolType.values()) {
      JRadioButton button = new JRadioButton(petrolType.getReadableName());
      button.setForeground(CustomColor.BUTTON_FOREGROUND);
      button.setFocusable(false);

      petrolTypeBtnGroup.add(button);
      view.getPetrolTypeSelectionView().add(button);
      addPetrolTypeSelectionListener(button, petrolType);
    }
  }

  private void addPetrolTypeSelectionListener(JRadioButton button, PetrolType petrolType) {
    button.setActionCommand(petrolType.name());

    button.addActionListener(e -> {
      JRadioButton selectedPetrolTypeButton = (JRadioButton) e.getSource();
      model.writeSelectionToUserPrefs(selectedPetrolTypeButton.getActionCommand());
    });
  }

  private void readSelectionFromUserPrefs() {
    List<AbstractButton> radioButtons = Collections.list(petrolTypeBtnGroup.getElements());

    for (AbstractButton radioButton : radioButtons) {
      if (model.readPreferredPetrolTypeName().equals(radioButton.getActionCommand())) {
        radioButton.setSelected(true);
        break;
      }
    }
  }
}
