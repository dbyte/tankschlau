package de.fornalik.tankschlau.gui;

import de.fornalik.tankschlau.TankSchlau;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Optional;

class CloseWindowMenuItem extends MenuItem {

  CloseWindowMenuItem() {
    super(TankSchlau.L10N.get("menu.CloseWindow"), KeyEvent.VK_W);
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
