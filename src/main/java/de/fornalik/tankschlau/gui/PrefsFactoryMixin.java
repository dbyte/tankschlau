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

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.text.*;
import java.awt.*;
import java.util.regex.Pattern;

/**
 * Mixin Interface for creating pre-factored user preference components.
 */
interface PrefsFactoryMixin {

  /**
   * Creates a customized <code>JLabel</code> instance with the specified text.
   * The label is aligned against the leading edge of its display area,
   * and centered vertically.
   *
   * @param text The text to be displayed by the label.
   * @return A custom formatted {@link JLabel}.
   */
  default JLabel createLabel(String text) {
    JLabel label = new JLabel(text);
    label.setForeground(CustomColor.LABEL_TEXT);
    return label;
  }

  /**
   * Creates a customized <code>JLabel</code> instance with the specified
   * text and horizontal alignment.
   * The label is centered vertically in its display area.
   *
   * @param text                The text to be displayed by the label.
   * @param horizontalAlignment One of the following constants
   *                            defined in <code>SwingConstants</code>:
   *                            <code>LEFT</code>,
   *                            <code>CENTER</code>,
   *                            <code>RIGHT</code>,
   *                            <code>LEADING</code> or
   *                            <code>TRAILING</code>.
   * @return A custom formatted {@link JLabel}.
   */
  default JLabel createLabel(String text, int horizontalAlignment) {
    JLabel label = new JLabel(text, horizontalAlignment);
    label.setForeground(CustomColor.LABEL_TEXT);
    return label;
  }

  /**
   * Creates a new customized titled border with the specified title.
   *
   * @param title a <code>String</code> containing the text of the title
   * @return the <code>TitledBorder</code> object
   */
  default TitledBorder createTitledBorder(String title) {
    TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
    titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(11f));
    titledBorder.setTitleColor(CustomColor.LABEL_TEXT);

    return titledBorder;
  }

  /**
   * Creates a new customized text field.
   *
   * @return A custom formatted/styled {@link JTextField}.
   */
  default JTextField createTextField() {
    JTextField field = new JTextField();
    field.setForeground(CustomColor.FIELD_TEXT);
    return field;
  }

  /**
   * Creates a new customized password field.
   *
   * @return A custom formatted/styled {@link JPasswordField}.
   */
  default JPasswordField createPasswordField() {
    JPasswordField field = new JPasswordField();
    field.setFont(new Font("monospaced", Font.PLAIN, 9));
    field.setForeground(CustomColor.PASSWORD_DOTS);
    return field;
  }

  /**
   * Creates a new default JTextField with an activated integer number filter.
   *
   * @param maxDigits Maximum allowed digits.
   * @return The instance of the new JTextField with an activated DocumentFilter.
   */
  default JTextField createNumbersOnlyTextField(int maxDigits) {
    if (maxDigits <= 0)
      throw new IllegalArgumentException("maxDigits must be greater than 0.");

    JTextField textField = new JTextField();
    ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumbersOnly(maxDigits));
    return textField;
  }

  /**
   * Filters only positive Integers of a JTextField with a given amount of max. digits.
   */
  class NumbersOnly extends DocumentFilter {
    final int maxDigits;
    final Pattern regex = Pattern.compile("^[0-9]+$");

    public NumbersOnly(int maxDigits) {
      this.maxDigits = maxDigits;
    }

    @Override
    public void replace(FilterBypass filterBypass, int offs, int length, String str, AttributeSet a)
    throws BadLocationException {

      Document doc = filterBypass.getDocument();
      String text = doc.getText(0, doc.getLength());
      text += str;

      if ((doc.getLength() + str.length() - length) <= maxDigits
          && text.matches(regex.pattern())) {
        super.replace(filterBypass, offs, length, str, a);
      }
      else {
        Toolkit.getDefaultToolkit().beep();
      }
    }

    @Override
    public void insertString(FilterBypass filterBypass, int offs, String str, AttributeSet a)
    throws BadLocationException {

      Document doc = filterBypass.getDocument();
      String text = doc.getText(0, doc.getLength());
      text += str;

      if ((doc.getLength() + str.length()) <= maxDigits && text.matches(regex.pattern())) {
        super.insertString(filterBypass, offs, str, a);
      }
      else {
        Toolkit.getDefaultToolkit().beep();
      }
    }
  }
}
