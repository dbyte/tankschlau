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

/**
 * User preferences panel for cyclic background services.
 */
class PrefsCyclePanel extends JPanel implements PrefsFactoryMixin {

  private static final Localization L10N = Localization.getInstance();
  private static final int minimumCycleRate = 3;
  private static final int defaultCycleRate = 300;
  private static final int defaultRowHeight = 25;
  private static final Dimension totalDimension = new Dimension(350, 70);

  private final JTextField textCycleRate;
  private final GridBagConstraints constraints;

  PrefsCyclePanel(UserPrefs userPrefs) {
    this.textCycleRate = createNumbersOnlyTextField(5);

    this.constraints = new GridBagConstraints();
    this.initView();
  }

  private void initView() {
    setLayout(new GridBagLayout());
    setOpaque(true);
    setBorder(createTitledBorder(L10N.get("borderTitle.AutomaticDataUpdate")));
    setMaximumSize(totalDimension);

    constraints.anchor = GridBagConstraints.WEST;
    constraints.fill = GridBagConstraints.HORIZONTAL;
    constraints.insets = new Insets(0, 5, 0, 5);

    constraints.gridy = 0; // Row
    createCycleRatePref();

    // ---> More rows go here <---
  }

  private void createCycleRatePref() {
    constraints.gridx = 0;
    JLabel labelCycleEvery = createLabel(L10N.get("label.CycleEvery"), SwingConstants.LEFT);
    addToPanel(labelCycleEvery, 120, constraints);

    constraints.gridx = 1;
    textCycleRate.setText(String.valueOf(defaultCycleRate));
    textCycleRate.addFocusListener(new CycleFocusListener());
    addToPanel(textCycleRate, 60, constraints);

    constraints.gridx = 2;
    addToPanel(createLabel(L10N.get("label.Seconds"), SwingConstants.LEFT), 100, constraints);
  }

  private void addToPanel(
      JComponent component,
      int width,
      GridBagConstraints constraints) {

    this.setSize(component, width);
    this.add(component, constraints);
  }

  private void setSize(JComponent component, int width) {
    component.setMinimumSize(new Dimension(width, defaultRowHeight));
    component.setMaximumSize(new Dimension(width, defaultRowHeight));
    component.setPreferredSize(new Dimension(width, defaultRowHeight));
  }

  /**
   * Corrects given cycle rate when losing focus (if necessary).
   */
  private static class CycleFocusListener implements FocusListener {
    @Override
    public void focusGained(FocusEvent e) {
      // Empty implementation
    }

    @Override
    public void focusLost(FocusEvent e) {
      if (!(e.getSource() instanceof JTextField))
        return;

      JTextField textField = (JTextField) e.getSource();
      if (textField.getDocument() == null)
        return;

      int length = textField.getDocument().getLength();
      if (length == 0)
        textField.setText(String.valueOf(defaultCycleRate));

      String text;
      try {
        text = textField.getDocument().getText(0, length);
        if (text == null || "".equals(text)) text = "0";
      }
      catch (BadLocationException ex) {
        ex.printStackTrace();
        text = "0";
      }

      if (Integer.parseInt(text) < minimumCycleRate) {
        Toolkit.getDefaultToolkit().beep();
        textField.setText(String.valueOf(defaultCycleRate));
      }
    }
  }
}