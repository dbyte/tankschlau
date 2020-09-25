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
