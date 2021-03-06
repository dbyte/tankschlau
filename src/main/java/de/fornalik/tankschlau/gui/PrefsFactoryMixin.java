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
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
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
   * @param text The text to be displayed for the checkbox.
   * @return A custom formatted {@link JCheckBox}.
   */
  default JCheckBox createEnableMessagesCheckbox(String text) {
    JCheckBox checkBox = new JCheckBox();
    checkBox.setText(text);
    checkBox.setFocusable(false);
    checkBox.setForeground(CustomColor.LABEL_TEXT);
    return checkBox;
  }

  /**
   * Creates a new customized titled border with the specified title.
   *
   * @param title a <code>String</code> containing the text of the title
   * @return the <code>TitledBorder</code> object
   */
  default TitledBorder createTitledBorder(String title) {
    TitledBorder titledBorder = BorderFactory.createTitledBorder(title);
    titledBorder.setTitleFont(titledBorder.getTitleFont().deriveFont(Font.PLAIN, 11f));
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
    field.setFont(new Font("monospaced", Font.PLAIN, 11));
    field.setForeground(CustomColor.PASSWORD_DOTS);
    return field;
  }

  /**
   * Creates a new default JTextField with an activated integer number filter.
   *
   * @param maxChars Maximum allowed characters.
   * @return The instance of the new JTextField with an activated DocumentFilter.
   */
  default JTextField createIntegerOnlyTextField(int maxChars) {
    if (maxChars <= 0)
      throw new IllegalArgumentException("maxChars must be greater than 0.");

    JTextField textField = new JTextField();
    ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumbersOnly(maxChars));
    return textField;
  }

  /**
   * Creates a new default JTextField with an activated integer or float number filter.
   *
   * @param maxChars Maximum allowed characters.
   * @return The instance of the new JTextField with an activated DocumentFilter.
   */
  default JTextField createIntegerOrFloatOnlyTextField(int maxChars) {
    if (maxChars <= 0)
      throw new IllegalArgumentException("maxChars must be greater than 0.");

    JTextField textField = new JTextField();
    ((AbstractDocument) textField.getDocument())
        .setDocumentFilter(new IntegerOrFloatOnly(maxChars));
    return textField;
  }

  /**
   * Filters only positive Floats or Integers of a JTextField with a given amount of
   * maximum characters.
   */
  class IntegerOrFloatOnly extends NumbersOnly {
    private final Pattern regex = Pattern.compile("^(0|[1-9]\\d*)(\\.\\d*)?$");

    IntegerOrFloatOnly(int maxDigits) {
      super(maxDigits);
    }

    @Override
    protected Pattern getRegex() {
      return regex;
    }
  }

  /**
   * Filters only positive Integers of a JTextField with a given amount of maximum characters.
   */
  class NumbersOnly extends DocumentFilter {
    protected final int maxChars;
    private final Pattern regex = Pattern.compile("^[0-9]+$");

    NumbersOnly(int maxChars) {
      this.maxChars = maxChars;
    }

    protected Pattern getRegex() {
      return regex;
    }

    // Source: https://stackoverflow.com/a/63964109

    @Override
    public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
      String text = fb.getDocument().getText(0, fb.getDocument().getLength());
      String newText = text.substring(0, offset) + text.substring(offset + length);

      if (newText.matches(getRegex().pattern()) || newText.length() == 0) {
        super.remove(fb, offset, length);
      }
    }

    @Override
    public void replace(
        FilterBypass fb,
        int offset,
        int length,
        String givenText,
        AttributeSet attrs)
    throws BadLocationException {

      String text = fb.getDocument().getText(0, fb.getDocument().getLength());
      String newText = text.substring(0, offset) + givenText + text.substring(offset + length);

      if (newText.length() <= maxChars && newText.matches(getRegex().pattern())) {
        super.replace(fb, offset, length, givenText, attrs);
      }
      else {
        Toolkit.getDefaultToolkit().beep();
      }
    }

    @Override
    public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
    throws BadLocationException {

      String text = fb.getDocument().getText(0, fb.getDocument().getLength());
      String newText = text.substring(0, offset) + string + text.substring(offset);

      if ((fb.getDocument().getLength() + string.length()) <= maxChars
          && newText.matches(getRegex().pattern())) {
        super.insertString(fb, offset, string, attr);
      }
      else {
        Toolkit.getDefaultToolkit().beep();
      }
    }
  }
}
