package de.fornalik.tankschlau.gui;

import de.fornalik.tankschlau.TankSchlau;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Optional;

class MainMenuBar extends JMenuBar {
  JFrame mainFrame;

  MainMenuBar(JFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.init();
  }

  void init() {
    // "File" menu
    JMenu fileMenu = new JMenu(TankSchlau.L10N.get("menu.File"));
    fileMenu.setMnemonic(KeyEvent.VK_F);

    // region "Close window" menu item - actually hides window.

    JMenuItem itemHideWindow = new JMenuItem(TankSchlau.L10N.get("menu.CloseWindow"));

    // Shortcut cmd + W: Hides active focused window if present.
    @SuppressWarnings("deprecation")
    KeyStroke strokeCmdW = KeyStroke.getKeyStroke(
        KeyEvent.VK_W,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    itemHideWindow.setAccelerator(strokeCmdW);

    itemHideWindow.addActionListener((event) -> {
      Optional<Window> window = Arrays
          .stream(Window.getWindows())
          .filter(Window::isFocused)
          .findFirst();

      window.ifPresent((w) -> w.setVisible(false));
    });

    // endregion

    // region "Show main window" menu item

    JMenuItem itemShowMainWindow = new JMenuItem(TankSchlau.L10N.get("menu.ShowMainWindow"));

    // Shortcut cmd + N: Shows main window if it's not visible
    @SuppressWarnings("deprecation")
    KeyStroke strokeCmdN = KeyStroke.getKeyStroke(
        KeyEvent.VK_N,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    itemShowMainWindow.setAccelerator(strokeCmdN);
    itemShowMainWindow.addActionListener((event) -> {
      if (!mainFrame.isVisible())
        mainFrame.setVisible(true);
    });

    // endregion

    fileMenu.add(itemHideWindow);
    fileMenu.add(itemShowMainWindow);

    this.add(fileMenu);
  }
}
