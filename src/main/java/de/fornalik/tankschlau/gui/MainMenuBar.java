package de.fornalik.tankschlau.gui;

import de.fornalik.tankschlau.util.Localization;

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
    JMenu fileMenu = new JMenu(Localization.get("File"));
    fileMenu.setMnemonic(KeyEvent.VK_F);

    // region cmd + W: Hides active focused window if present.

    JMenuItem itemClose = new JMenuItem(Localization.get("CloseWindow"));
    KeyStroke strokeCmdW = KeyStroke.getKeyStroke(
        KeyEvent.VK_W,
        Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
    itemClose.setAccelerator(strokeCmdW);

    itemClose.addActionListener((event) -> {
      Optional<Window> window = Arrays
          .stream(Window.getWindows())
          .filter(Window::isFocused)
          .findFirst();

      window.ifPresent(w -> w.setVisible(false));
    });

    // endregion

    // region cmd + N: Shows main window if it's not visible

    JMenuItem itemNewMain = new JMenuItem(Localization.get("ShowMainWindow"));
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
