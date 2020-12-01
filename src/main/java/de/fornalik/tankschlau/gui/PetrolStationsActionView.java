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

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * Shows user actions (buttons etc.) for driving the Petrol Stations table view.
 */
@org.springframework.stereotype.Component
class PetrolStationsActionView extends JPanel {

  private static final Localization L10N = Localization.getInstance();

  private final PetrolTypeView petrolTypeView;
  private final JButton btnStartOneShotWork;
  private final JButton btnStartCyclicWork;
  private final JButton btnRemoveAllData;

  @Autowired
  PetrolStationsActionView(PetrolTypeView petrolTypeView) {
    this.petrolTypeView = petrolTypeView;

    this.btnStartOneShotWork = new JButton();
    this.btnStartCyclicWork = new JButton();
    this.btnRemoveAllData = new JButton();
  }

  @PostConstruct
  private void initView() {
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    setAlignmentX(Component.CENTER_ALIGNMENT);
    setPreferredSize(new Dimension(190, this.getHeight()));
    setMaximumSize(new Dimension(190, Short.MAX_VALUE));

    add(Box.createRigidArea(new Dimension(getMaximumSize().width, 52)));
    addButton(btnStartOneShotWork, L10N.get("button.UpdateOnce"));

    add(Box.createRigidArea(new Dimension(getMaximumSize().width, 5)));
    addButton(btnStartCyclicWork, L10N.get("button.UpdateCyclic"));

    add(Box.createRigidArea(new Dimension(getMaximumSize().width, 5)));
    addButton(btnRemoveAllData, L10N.get("button.EmptyTableView"));

    add(Box.createRigidArea(new Dimension(getMaximumSize().width, 10)));
    add(createSeparator());
    add(createPetrolTypeChooser());
    add(Box.createVerticalGlue());
  }

  private void addButton(JButton btn, String title) {
    btn.setText(title);
    btn.setMinimumSize(new Dimension(getMaximumSize().width, 25));
    btn.setMaximumSize(new Dimension(getMaximumSize().width, 25));
    btn.setPreferredSize(new Dimension(getMaximumSize().width, 25));
    btn.setForeground(CustomColor.BUTTON_FOREGROUND);
    btn.setFocusable(false);
    btn.setAlignmentX(CENTER_ALIGNMENT);
    add(btn);
  }

  private JPanel createPetrolTypeChooser() {
    petrolTypeView.setMinimumSize(new Dimension(getMaximumSize().width, 100));
    petrolTypeView.setMaximumSize(new Dimension(getMaximumSize().width, 100));
    petrolTypeView.setPreferredSize(new Dimension(getMaximumSize().width, 100));
    petrolTypeView.setAlignmentX(CENTER_ALIGNMENT);

    return petrolTypeView;
  }

  private JSeparator createSeparator() {
    JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
    separator.setAlignmentX(CENTER_ALIGNMENT);
    separator.setMinimumSize(new Dimension(getMaximumSize().width, 10));
    separator.setMaximumSize(new Dimension(getMaximumSize().width, 10));

    return separator;
  }

  JButton getBtnStartOneShotWork() {
    return btnStartOneShotWork;
  }

  JButton getBtnStartCyclicWork() {
    return btnStartCyclicWork;
  }

  JButton getBtnRemoveAllData() {
    return btnRemoveAllData;
  }
}
