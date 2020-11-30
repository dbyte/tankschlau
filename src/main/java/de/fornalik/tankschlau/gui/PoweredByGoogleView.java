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
import java.util.logging.Logger;

/**
 * This JPanel provides support for the Google Copyright Icon.
 */
class PoweredByGoogleView extends JPanel {

  private static final Logger LOGGER = Logger.getLogger(PoweredByGoogleView.class.getName());

  PoweredByGoogleView() {
    super();
    initView();
  }

  private void initView() {
    setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));

    add(createGoogleLabel());
  }

  private JLabel createGoogleLabel() {
    JLabel label = new JLabel();

    label.setAlignmentY(CENTER_ALIGNMENT);
    label.setAlignmentX(CENTER_ALIGNMENT);
    label.setIcon(readPoweredByGoogleIcon());

    return label;
  }

  private ImageIcon readPoweredByGoogleIcon() {
    ImageIcon icon = new ImageIcon(); // Setup empty image in case reading fails

    URL iconPath = PoweredByGoogleView.class.getClassLoader()
        .getResource("powered_by_google_on_white.png");

    if (iconPath != null) {
      icon = new ImageIcon(iconPath);
    }
    else {
      LOGGER.warning("Google image icon not found in ClassLoader path.");
    }

    return icon;
  }
}
