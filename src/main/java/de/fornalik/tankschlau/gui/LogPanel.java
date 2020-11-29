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
import de.fornalik.tankschlau.util.LoggingConfig;
import de.fornalik.tankschlau.util.SwingLoggingHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.swing.*;
import java.awt.*;

/**
 * Shows and drives logging. The log records are represented in a JTextArea by a custom Log Handler.
 */
@Component
class LogPanel extends JPanel {

  private final Localization l10n;
  private final JLabel labelLogHeader;
  private final JTextArea textAreaLog;
  private final JButton btnClearLog;

  @Autowired
  LogPanel(Localization l10n) {
    super();
    this.l10n = l10n;
    this.labelLogHeader = new JLabel();
    this.textAreaLog = new JTextArea();
    this.btnClearLog = new JButton();
  }

  @PostConstruct
  private void initView() {
    setLayout(new BorderLayout(5, 5));
    setOpaque(true);
    setMinimumSize(new Dimension(0, 100));

    add(createMainPanel(), BorderLayout.CENTER);
    add(createLogButtonPanel(), BorderLayout.LINE_START);

    // Bind logging handler to our TextArea
    ((SwingLoggingHandler) LoggingConfig.SWING_LOGGING_HANDLER).setTextArea(textAreaLog);
  }

  private JPanel createMainPanel() {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

    panel.add(createLogHeaderPanel());
    panel.add(createLogScrollPane());

    return panel;
  }

  private JPanel createLogHeaderPanel() {
    labelLogHeader.setText(l10n.get("label.Log"));
    labelLogHeader.setForeground(CustomColor.BOX_HEADER_TEXT);

    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panel.add(labelLogHeader);
    panel.setMaximumSize(new Dimension(getMaximumSize().width, 25));

    return panel;
  }

  private JScrollPane createLogScrollPane() {
    textAreaLog.setEditable(false);
    textAreaLog.setLineWrap(true);
    textAreaLog.setFont(new Font("monospaced", Font.PLAIN, 13));
    textAreaLog.setForeground(CustomColor.LOG_TEXT);

    JScrollPane scrollPane = new JScrollPane(textAreaLog);
    scrollPane.setBorder(BorderFactory.createLineBorder(Color.getHSBColor(0f, 0f, 0.80f)));

    return scrollPane;
  }

  private JPanel createLogButtonPanel() {
    final int maxWidth = 190;
    final int buttonHeight = 25;

    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setMaximumSize(new Dimension(maxWidth, getHeight()));

    panel.add(Box.createRigidArea(new Dimension(maxWidth, 27)));
    panel.add(btnClearLog);

    btnClearLog.setAlignmentX(CENTER_ALIGNMENT);
    btnClearLog.setText(l10n.get("button.ClearLogView"));
    btnClearLog.setMinimumSize(new Dimension(maxWidth, buttonHeight));
    btnClearLog.setMaximumSize(new Dimension(maxWidth, buttonHeight));
    btnClearLog.setPreferredSize(new Dimension(maxWidth, buttonHeight));
    btnClearLog.setForeground(CustomColor.BUTTON_FOREGROUND);
    btnClearLog.setFocusable(false);

    return panel;
  }

  JTextArea getTextAreaLog() {
    return textAreaLog;
  }

  JButton getBtnClearLog() {
    return btnClearLog;
  }
}
