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

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Base class for all menu items within the application.
 */
abstract class MenuItem extends JMenuItem {
  protected final int keyEvent;

  /**
   * @param text     Representational text of the {@link JMenuItem}.
   * @param keyEvent The pressed key (without modifier) on which to trigger the action of the item.
   *                 Use one of the {@link KeyEvent} constants.
   */
  protected MenuItem(String text, int keyEvent) {
    super(text);
    this.keyEvent = keyEvent;
    this.init();
  }

  private void init() {
    this.setAccelerator(createCtrlKeystroke());
    this.addActionListener(this::defineAction);
  }

  @SuppressWarnings("deprecation")
  private KeyStroke createCtrlKeystroke() {
    // cmd/ctrl + W: Hides active focused window if present.
    return KeyStroke.getKeyStroke(
        keyEvent,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
  }

  protected abstract void defineAction(ActionEvent e);
}
