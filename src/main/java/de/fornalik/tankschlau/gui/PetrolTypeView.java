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
 * Chooser for preferred PetrolType.
 */
@Component
class PetrolTypeView extends JPanel {

  private static final Localization L10N = Localization.getInstance();
  private final JPanel petrolTypeSelectionView;

  @Autowired
  PetrolTypeView() {
    this.petrolTypeSelectionView = new JPanel();
  }

  @PostConstruct
  private void initView() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    configurePetrolTypeSelectionPanel();

    add(createHeaderLabel());
    add(petrolTypeSelectionView);
  }

  private JPanel createHeaderLabel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel label = new JLabel(L10N.get("label.PreferredPetrolType"));
    label.setForeground(CustomColor.LABEL_TEXT);

    panel.add(label);
    return panel;
  }

  private void configurePetrolTypeSelectionPanel() {
    petrolTypeSelectionView.setLayout(new GridLayout(0, 1));
  }

  JPanel getPetrolTypeSelectionView() {
    return petrolTypeSelectionView;
  }
}
