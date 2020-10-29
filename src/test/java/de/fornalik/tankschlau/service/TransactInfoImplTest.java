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

package de.fornalik.tankschlau.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * We do only test getters/setters with non-standard functionality here.
 */
class TransactInfoImplTest {
  private TransactInfoImpl transactInfo; // SUT

  @BeforeEach
  void setUp() {
    transactInfo = new TransactInfoImpl();
  }

  @Test
  void construct_setsExpectedFieldValues() {
    // when
    transactInfo = new TransactInfoImpl();

    // then
    assertEquals(Optional.empty(), transactInfo.getErrorMessage());
    assertEquals("", transactInfo.getLicence());
    assertEquals("", transactInfo.getStatus());
  }

  @Test
  void getStatus_returnsEmptyStringWhenFieldIsNull() {
    // given
    transactInfo.setStatus(null);

    // when
    String actualStatus = transactInfo.getStatus();

    // then
    assertEquals("", actualStatus);
  }

  @Test
  void setStatus_setsFieldValue() {
    // given
    transactInfo.setStatus("Some status value");

    // when
    String actualStatus = transactInfo.getStatus();

    // then
    assertEquals("Some status value", actualStatus);
  }

  @Test
  void getErrorMessage_returnsEmptyOptionalIfFieldIsNull() {
    // given
    transactInfo.setErrorMessage(null);

    // when
    Optional<String> actualErrorMessage = transactInfo.getErrorMessage();

    // then
    assertEquals(Optional.empty(), actualErrorMessage);
  }

  @Test
  void getErrorMessage_returnsValueIfFieldIsNotNull() {
    // given
    transactInfo.setErrorMessage("Some error message");

    // when
    Optional<String> actualErrorMessage = transactInfo.getErrorMessage();

    // then
    //noinspection OptionalGetWithoutIsPresent
    assertEquals("Some error message", actualErrorMessage.get());
  }

  @Test
  void setErrorMessage_safelyTrimsNullAndNonNullString() {
    // given
    transactInfo.setErrorMessage("  This error message has to be trimmed    ");
    // when
    Optional<String> actualErrorMessage = transactInfo.getErrorMessage();
    // then
    //noinspection OptionalGetWithoutIsPresent
    assertEquals("This error message has to be trimmed", actualErrorMessage.get());

    // when / then
    assertDoesNotThrow(() -> transactInfo.setErrorMessage(null));
  }

  @Test
  void reset_setsSameInitialValuesAsConstructor() {
    // given
    TransactInfoImpl initialObject = new TransactInfoImpl();

    transactInfo.setErrorMessage("Some error value");
    transactInfo.setStatus("Some status value");
    transactInfo.setLicence("Some licence value");

    // when
    transactInfo.reset();

    // then
    assertEquals(initialObject.getErrorMessage(), transactInfo.getErrorMessage());
    assertEquals(initialObject.getLicence(), transactInfo.getLicence());
    assertEquals(initialObject.getStatus(), transactInfo.getStatus());
  }
}