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

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.logging.LogManager;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggingConfigTest {

  @BeforeAll
  static void beforeAll() {
    LogManager.getLogManager().reset();
  }

  @AfterAll
  static void afterAll() {
    LogManager.getLogManager().reset();
  }

  @Test
  void init_shouldSetConsoleHandlerFormatterToUseCustomFormatter() {
    // given
    String expectedPropertyValue = "de.fornalik.tankschlau.util.LoggingFormatter";

    // when
    LoggingConfig.init();
    String actualPropertyValue = LogManager.getLogManager()
        .getProperty("java.util.logging.ConsoleHandler.formatter");

    // then
    assertEquals(expectedPropertyValue, actualPropertyValue);
  }
}