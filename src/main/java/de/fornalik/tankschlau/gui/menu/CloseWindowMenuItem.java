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

import de.fornalik.tankschlau.bootstrap.AppContainer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Optional;

/**
 * Closes the window which currently has focus.
 */
class CloseWindowMenuItem extends MenuItem {

  CloseWindowMenuItem() {
    super(AppContainer.L10N.get("menu.CloseWindow"), KeyEvent.VK_W);
  }

  @Override
  protected void defineAction(ActionEvent e) {
    {
      Optional<Window> window = Arrays
          .stream(Window.getWindows())
          .filter(Window::isFocused)
          .findFirst();

      window.ifPresent((w) -> w.setVisible(false));
    }
  }
}
