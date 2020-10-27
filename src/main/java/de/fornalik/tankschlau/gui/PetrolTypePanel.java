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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Chooser for preferred PetrolType.
 */
class PetrolTypePanel extends JPanel implements ActionListener {

  PetrolTypePanel() {

    this.initView();
  }

  private void initView() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

    add(createHeaderLabel());
    add(createRadioPanel());
  }

  private JPanel createHeaderLabel() {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    JLabel label = new JLabel("Preferred Petrol Type");
    label.setForeground(CustomColor.LABEL_TEXT);

    panel.add(label);
    return panel;
  }

  private JPanel createRadioPanel() {
    JPanel panel = new JPanel(new GridLayout(0, 1));
    PetrolType[] petrolTypes = PetrolType.values();
    ButtonGroup buttonGroup = new ButtonGroup();

    for (PetrolType petrolType : petrolTypes) {
      JRadioButton radioButton = new JRadioButton(petrolType.getReadableName());
      radioButton.setForeground(CustomColor.BUTTON_FOREGROUND);
      radioButton.addActionListener(this);
      buttonGroup.add(radioButton);
      panel.add(radioButton);
    }

    return panel;
  }

  @Override
  public void actionPerformed(ActionEvent e) {

  }
}
