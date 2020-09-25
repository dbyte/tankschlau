package de.fornalik.tankschlau.gui.menu;

import de.fornalik.tankschlau.TankSchlau;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * Switches the main frame window visible if not visible.
 */
class ShowMainWindowMenuItem extends MenuItem {
  private final JFrame mainFrame;

  ShowMainWindowMenuItem(JFrame mainFrame) {
    super(TankSchlau.L10N.get("menu.ShowMainWindow"), KeyEvent.VK_N);
    this.mainFrame = mainFrame;
  }

  @Override
  protected void defineAction(ActionEvent e) {
    if (!mainFrame.isVisible())
      mainFrame.setVisible(true);
  }
}
