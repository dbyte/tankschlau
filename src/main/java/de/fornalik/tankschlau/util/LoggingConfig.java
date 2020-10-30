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

package de.fornalik.tankschlau.util;

import java.io.InputStream;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Provides configuration of the used logging system.
 */
public class LoggingConfig {
  // Create our custom handler which sends log messages to a Swing TextArea.
  public static final Handler SWING_LOGGING_HANDLER = new SwingLoggingHandler();

  /**
   * Call once at boot time!
   * Implicitly calls constructor of <code>LoggingFormatter</code>, if logging.properties
   */
  public static void init() {
    LogManager globalLogManager = LogManager.getLogManager();
    globalLogManager.reset();

    try {
      // Read & parse from logging.properties. ...
      InputStream stream = LoggingConfig.class.getClassLoader()
          .getResourceAsStream("logging.properties");
      // ... to the LogManager singleton
      globalLogManager.readConfiguration(stream);

      // Here is a hook to bind our handler with a custom formatter:
      SWING_LOGGING_HANDLER.setFormatter(new LoggingFormatter());
      //swingLogHandler.setLevel(Level.INFO);  // Better set it in properties

      // Determine the overall context for the handler and create a logger with our custom
      // handler for that context.
      Logger appContextLogger = Logger.getLogger("de.fornalik.tankschlau");
      appContextLogger.addHandler(SWING_LOGGING_HANDLER);

      // Add logger (and implicitly its handler) to the global LogManager
      globalLogManager.addLogger(appContextLogger);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

    Logger logger = Logger.getLogger(LoggingConfig.class.getName());
    logger.finest("Logger initialized.");
  }
}
