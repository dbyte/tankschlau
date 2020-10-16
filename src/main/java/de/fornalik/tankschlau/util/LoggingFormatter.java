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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

/**
 * Supports applying a custom logging {@link Formatter} to a specified {@link Handler}.
 * <b>NOTE:</b> Class must remain public to be accessible through logging.properties file.
 */
public class LoggingFormatter extends Formatter {
  private static final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd kk:mm:ss.SSS");
  private static boolean useSimpleClassName = false;

  /**
   * Constructor gets called once, implicitly by the native {@link java.util.logging.LogManager},
   * when a LoggingFormatter entry gets assigned to a {@link java.util.logging.Handler} within the
   * logging.properties file.
   * <p>
   * The above process can be started by calling {@link LoggingConfig} while
   * bootstrapping the app.
   */
  public LoggingFormatter() {
    super();

    LogManager logManager = LogManager.getLogManager();
    String thisClassName = getClass().getName();

    // Get custom properties for this formatter from logging.properties.

    useSimpleClassName = "yes"
        .equals(logManager.getProperty(thisClassName + ".useSimpleClassName"));
  }

  public String format(LogRecord record) {
    StringBuilder builder = new StringBuilder(50);

    builder.append(dateFormat.format(new Date(record.getMillis()))).append(" - ");

    if (useSimpleClassName) {
      builder.append("[").append(extractSimpleSourceClassName(record)).append(".");
    }
    else {
      builder.append("[").append(record.getSourceClassName()).append(".");
    }

    builder.append(record.getSourceMethodName()).append("] - ");
    builder.append("[").append(record.getLevel()).append("] ");
    builder.append(formatMessage(record));
    builder.append("\n");

    return builder.toString();
  }

  public String getHead(Handler h) {
    return super.getHead(h);
  }

  public String getTail(Handler h) {
    return super.getTail(h);
  }

  private String extractSimpleSourceClassName(LogRecord record) {
    // Converts e.g. "some.package.path.TankSchlau" to "TankSchlau".
    String sourceClassName = record.getSourceClassName();
    int lastDot = sourceClassName.lastIndexOf(".");

    if (lastDot > -1 && lastDot < sourceClassName.length())
      return sourceClassName.substring(lastDot + 1);

    return sourceClassName;
  }
}
