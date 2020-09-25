package de.fornalik.tankschlau.gui;

import de.fornalik.tankschlau.TankSchlau;

import javax.swing.*;
import java.awt.event.KeyEvent;

class MainMenuBar extends JMenuBar {
  JFrame mainFrame;

  MainMenuBar(JFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.init();
  }

  private void init() {
    // "File" menu
    JMenu fileMenu = new JMenu(TankSchlau.L10N.get("menu.File"));
    fileMenu.setMnemonic(KeyEvent.VK_F);

    fileMenu.add(new CloseWindowMenuItem());
    fileMenu.add(new ShowMainWindowMenuItem(mainFrame));

    this.add(fileMenu);
  }
}
