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

package de.fornalik.tankschlau.gui.menu;

import de.fornalik.tankschlau.util.Localization;

import javax.swing.*;
import java.awt.event.KeyEvent;

public class MainMenuBar extends JMenuBar {
  private final Localization l10n;
  JFrame mainFrame;

  public MainMenuBar(JFrame mainFrame, Localization l10n) {
    this.mainFrame = mainFrame;
    this.l10n = l10n;

    this.init();
  }

  private void init() {
    // "File" menu
    JMenu fileMenu = new JMenu(l10n.get("menu.File"));
    fileMenu.setMnemonic(KeyEvent.VK_F);

    fileMenu.add(new CloseWindowMenuItem(l10n));
    fileMenu.add(new ShowMainWindowMenuItem(mainFrame, l10n));

    this.add(fileMenu);
  }
}
