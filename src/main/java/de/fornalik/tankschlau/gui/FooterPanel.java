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
import java.awt.*;
import java.net.URL;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Shows application-wide information for the current state of the app and its background workers.
 * It can be driven by various controllers.
 */
class FooterPanel extends JPanel {

  private static final Logger LOGGER = Logger.getLogger(FooterPanel.class.getName());

  private final JLabel labelCountdown, labelWork;
  private final ImageIcon iconWork;

  FooterPanel() {
    super(new GridLayout(1, 3));

    this.labelCountdown = new JLabel();
    this.labelWork = new JLabel();
    this.iconWork = this.readWorkIndicatorIcon();

    this.initView();
  }

  private void initView() {
    add(labelCountdown);
    add(labelWork);
    add(new JLabel()); // spare

    setBackground(Color.DARK_GRAY);
    setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
    setOpaque(true);

    configureCountdownLabel();
    configureWorkLabel();
  }

  private void configureCountdownLabel() {
    labelCountdown.setText("Cyclic worker deactivated");
    labelCountdown.setHorizontalAlignment(SwingConstants.LEFT);
    labelCountdown.setFont(labelCountdown.getFont().deriveFont(12F));
    labelCountdown.setForeground(Color.getHSBColor(0F, 0F, 0.76F));
  }

  private void configureWorkLabel() {
    labelWork.setText("No activity");
    labelWork.setIcon(null);
    labelWork.setPreferredSize(new Dimension(getMaximumSize().width, 40));
    labelWork.setHorizontalAlignment(SwingConstants.CENTER);
    labelWork.setHorizontalTextPosition(SwingConstants.LEFT);
    labelWork.setIconTextGap(10);
    labelWork.setFont(labelWork.getFont().deriveFont(12F));
    labelWork.setForeground(Color.getHSBColor(0F, 0F, 0.76F));

    iconWork.setImageObserver(labelWork);
  }

  private ImageIcon readWorkIndicatorIcon() {
    ImageIcon icon = new ImageIcon(); // Setup empty image in case reading fails

    // Original image source: http://www.ajaxload.info
    URL iconPath = getClass().getClassLoader().getResource("loader-on-dark-gray.gif");

    if (iconPath != null) {
      icon = new ImageIcon(iconPath);
    }
    else {
      LOGGER.warning("Image icon not found in ClassLoader path.");
    }

    return icon;
  }

  void onOneShotWorkerStarted(String name) {
    String legalizedName = name != null ? name : "";
    labelWork.setText((legalizedName + " Worker running").trim());
    labelWork.setIcon(iconWork);
  }

  void onOneShotWorkerFinished() {
    labelWork.setText("No activity");
    labelWork.setIcon(null);
  }

  void onCyclicWorkerStopped() {
    labelCountdown.setText("Cyclic worker deactivated");
    labelWork.setText("No activity");
    labelWork.setIcon(null);
  }

  void updateCountdown(long remaining, TimeUnit timeUnit) {
    final String textForCyclicWorker;
    final String textForSingleWorker;
    final ImageIcon workerIndicator;

    if (remaining <= 0) {
      textForCyclicWorker = "Waiting for Worker to finish...";
      textForSingleWorker = "Worker running";
      workerIndicator = iconWork;
    }

    else {
      textForCyclicWorker =
          "Worker starts in: " + remaining + " " + timeUnit.name().toLowerCase();
      textForSingleWorker = "No activity";
      workerIndicator = null;
    }

    labelCountdown.setText(textForCyclicWorker);
    labelWork.setText(textForSingleWorker);
    labelWork.setIcon(workerIndicator);
  }
}
