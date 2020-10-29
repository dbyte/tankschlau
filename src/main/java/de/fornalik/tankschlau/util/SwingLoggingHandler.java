package de.fornalik.tankschlau.util;

import javax.swing.*;
import java.util.logging.LogRecord;

// TODO unit tests

public class SwingLoggingHandler extends java.util.logging.Handler {
  private JTextArea textArea;

  @Override
  public void publish(LogRecord record) {
    // Make sure preconditions for logging are valid.
    if (!isLoggable(record))
      return;

    // Use the injectable formatter delegate to format the message properly.

    if (SwingUtilities.isEventDispatchThread()) {
      // We're already on the EDT, no need to add something to its queue.
      textArea.append(getFormatter().format(record));
    }
    else {
      // Call comes from different thread than EDT. Put the textArea manipulation operation on the
      // EDT queue. That way, Swing will complete all of its own pending Events before
      // manipulating the textArea.
      SwingUtilities.invokeLater(() -> textArea.append(getFormatter().format(record)));
    }
  }

  @Override
  public boolean isLoggable(LogRecord record) {
    return super.isLoggable(record) && textArea != null;
  }

  @Override
  public void flush() {
  }

  @Override
  public void close() throws SecurityException {
  }

  public void setTextArea(JTextArea textArea) {
    this.textArea = textArea;
  }
}
