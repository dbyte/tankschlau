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
    JMenu fileMenu = new JMenu(TankSchlau.L10N.get("File"));
    fileMenu.setMnemonic(KeyEvent.VK_F);

    // region "Close window" menu item

    JMenuItem itemClose = new JMenuItem(TankSchlau.L10N.get("CloseWindow"));

    // Shortcut cmd + W: Hides active focused window if present.
    KeyStroke strokeCmdW = KeyStroke.getKeyStroke(
        KeyEvent.VK_W,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    itemClose.setAccelerator(strokeCmdW);

    itemClose.addActionListener((event) -> {
      Optional<Window> window = Arrays
          .stream(Window.getWindows())
          .filter(Window::isFocused)
          .findFirst();

      window.ifPresent((w) -> w.setVisible(false));
    });

    // endregion

    // region "Show main window" menu item

    JMenuItem itemNewMain = new JMenuItem(TankSchlau.L10N.get("ShowMainWindow"));

    // Shortcut cmd + N: Shows main window if it's not visible
    KeyStroke strokeCmdN = KeyStroke.getKeyStroke(
        KeyEvent.VK_N,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    itemNewMain.setAccelerator(strokeCmdN);
    itemNewMain.addActionListener((event) -> {
      if (!mainFrame.isVisible())
        mainFrame.setVisible(true);
    });

    // endregion

    fileMenu.add(itemClose);
    fileMenu.add(itemNewMain);

    this.add(fileMenu);
  }
}
