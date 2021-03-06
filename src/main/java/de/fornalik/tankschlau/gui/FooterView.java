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
import java.net.URL;
import java.util.logging.Logger;

/**
 * Shows application-wide information for the current state of the app and its background workers.
 * It can be driven by various controllers.
 */
@Component
class FooterView extends JPanel {

  private static final Logger LOGGER = Logger.getLogger(FooterView.class.getName());
  private static final Localization L10N = Localization.getInstance();

  private final JLabel labelCountdown;
  private final JLabel labelWork;
  private final ImageIcon iconWork;

  @Autowired
  FooterView() {
    super();
    this.labelCountdown = createBaseLabel();
    this.labelWork = createBaseLabel();
    this.iconWork = this.readWorkIndicatorIcon();
  }

  @PostConstruct
  private void initView() {
    setLayout(new GridLayout(1, 3));
    setBackground(Color.DARK_GRAY);
    setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
    setOpaque(true);

    add(labelCountdown);
    add(labelWork);
    add(createJavaVersionLabel());

    configureCountdownLabel();
    configureWorkLabel();
  }

  private void configureCountdownLabel() {
    labelCountdown.setText(L10N.get("label.AutoUpdateStopped"));
    labelCountdown.setHorizontalAlignment(SwingConstants.LEFT);
  }

  private void configureWorkLabel() {
    labelWork.setText(L10N.get("label.NoNetworkActivity"));
    labelWork.setIcon(null);
    labelWork.setPreferredSize(new Dimension(getMaximumSize().width, 40));
    labelWork.setHorizontalAlignment(SwingConstants.CENTER);
    labelWork.setHorizontalTextPosition(SwingConstants.LEFT);
    labelWork.setIconTextGap(10);

    iconWork.setImageObserver(labelWork);
  }

  private JLabel createJavaVersionLabel() {
    JLabel label = createBaseLabel();
    label.setText("Java " + System.getProperty("java.version"));
    label.setHorizontalAlignment(SwingConstants.RIGHT);
    label.setHorizontalTextPosition(SwingConstants.RIGHT);
    return label;
  }

  private JLabel createBaseLabel() {
    JLabel label = new JLabel();
    label.setFont(label.getFont().deriveFont(12F));
    label.setForeground(Color.getHSBColor(0F, 0F, 0.76F));
    return label;
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

  JLabel getLabelCountdown() {
    return labelCountdown;
  }

  JLabel getLabelWork() {
    return labelWork;
  }

  ImageIcon getIconWork() {
    return iconWork;
  }
}
