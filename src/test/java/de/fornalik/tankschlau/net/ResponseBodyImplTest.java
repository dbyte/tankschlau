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

package de.fornalik.tankschlau.net;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ResponseBodyImplTest {
  private ResponseBodyImpl responseBody;

  @BeforeEach
  void setUp() {
    responseBody = new ResponseBodyImpl(); // SUT
  }

  @Test
  void getData_castsToProperValueIfValueIsPresent() throws IOException {
    // given
    ByteArrayInputStream expectedStream = new ByteArrayInputStream(
        new byte[]{0b1000100, 0b1011011});

    responseBody.setData(expectedStream);

    // when
    ByteArrayInputStream actualStream = responseBody.getData(ByteArrayInputStream.class);

    // then
    assertEquals(expectedStream, actualStream);
    expectedStream.close();
  }

  @Test
  void getData_throwsOnNullArgument() {
    assertThrows(NullPointerException.class, () -> responseBody.getData(null));
  }

  @Test
  void setData_AcceptsNullArgument() {
    assertDoesNotThrow(() -> responseBody.setData(null));
    assertNull(responseBody.getData(String.class));
  }

  @Test
  void setData_setsDataFieldProperly() {
    // given
    String givenString = "This is a string which represents body data";
    assert responseBody.getData(String.class) == null;

    // when then
    assertDoesNotThrow(() -> responseBody.setData(givenString));
    assertEquals(givenString, responseBody.getData(String.class));
  }

  @Test
  void reset_setsDataFieldToNull() {
    // given
    ResponseBodyImpl responseBody = new ResponseBodyImpl();

    responseBody.setData("Some value");
    assert "Some value".equals(responseBody.getData(String.class));

    // when
    responseBody.reset();

    // then
    assertNull(responseBody.getData(String.class));
  }
}