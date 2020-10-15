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

package de.fornalik.tankschlau.webserviceapi.pushover;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * We do only test getters/setters with non-standard functionality here.
 */
class PushoverMessageContentTest {
  private PushoverMessageContent sut;
  private String actualString;

  @BeforeEach
  void setUp() {
    sut = new PushoverMessageContent();
    actualString = null;
  }

  @Test
  void getTitle_returnsEmptyStringIfTitleIsNull() {
    // given
    sut.setTitle(null);

    // when
    actualString = sut.getTitle();

    // then
    assertEquals("", actualString);
  }

  @Test
  void getTitle_returnsProperValueWhenFieldIsNotNull() {
    // given
    sut.setTitle("Some value 1");

    // when
    actualString = sut.getTitle();

    // then
    assertEquals("Some value 1", actualString);
  }

  @Test
  void getMessage_returnsEmptyStringIfTitleIsNull() {
    // given
    sut.setMessage(null);

    // when
    actualString = sut.getMessage();

    // then
    assertEquals("", actualString);
  }

  @Test
  void getMessage_returnsProperValueWhenFieldIsNotNull() {
    // given
    sut.setMessage("Some value 2");

    // when
    actualString = sut.getMessage();

    // then
    assertEquals("Some value 2", actualString);
  }

  @Test
  void reset_doesReturnSameObject() {
    // given
    sut.setMessage("Some message 3");
    sut.setTitle("Some title 3");
    PushoverMessageContent objReferenceBeforeReset = sut;

    // Validate test preconditions
    assert sut.getMessage() != null;
    assert sut.getTitle() != null;

    // when
    sut.reset();

    // then
    assertSame(objReferenceBeforeReset, sut);
  }

  @Test
  void reset_properlyInitializesTheObject() {
    // given
    sut.setMessage("Some message 4");
    sut.setTitle("Some title 4");

    // Validate test preconditions
    assert sut.getMessage() != null;
    assert sut.getTitle() != null;

    // when
    sut.reset();

    // then
    assertEquals("", sut.getMessage());
    assertEquals("", sut.getTitle());
  }
}