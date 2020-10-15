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

package de.fornalik.tankschlau.webserviceapi.common;

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.Petrol;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.util.Localization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class PetrolStationMessageContentTest {
  private PetrolStationMessageContent messageContentStub; // SUT
  private Localization l10nSingleton;
  private PetrolStation petrolStationMock;
  private Petrol petrolMock;
  private Geo geoMock;

  @BeforeEach
  void setUp() {
    petrolStationMock = mock(PetrolStation.class, Mockito.RETURNS_DEEP_STUBS);
    petrolMock = mock(Petrol.class);
    geoMock = mock(Geo.class);

    l10nSingleton = Localization.getInstance();
    messageContentStub = new PetrolStationMessageContentStub();
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void setMessage_fromPetrolStationData_formatsContentAsExpected(boolean stationIsOpen) {
    // given
    l10nSingleton.configure(Locale.GERMANY);

    String expectedBestPriceString = "DIESEL: 1,219 €";
    String expectedHeadline = l10nSingleton.get("msg.BestPrice", expectedBestPriceString);
    String expectedDistanceString = "5.3 km entfernt";
    String expectedPetrolStationName = "Tankstelle Gluckenkopp";
    String expectedStreetName = "Schlampstrasse 55";
    String expectedOpenedString = "jetzt geöffnet";
    String expectedClosedString = "geschlossen";
    String expectedOpenCloseString = stationIsOpen ? expectedOpenedString : expectedClosedString;

    when(geoMock.getDistanceAwayString()).thenReturn(expectedDistanceString);

    when(petrolStationMock.findPetrol(any())).thenReturn(Optional.ofNullable(petrolMock));
    when(petrolMock.getTypeAndPrice()).thenReturn(expectedBestPriceString);

    when(petrolStationMock.isOpen()).thenReturn(stationIsOpen);
    when(petrolStationMock.getAddress().getName()).thenReturn(expectedPetrolStationName);
    when(petrolStationMock.getAddress().getGeo()).thenReturn(Optional.of(geoMock));
    when(petrolStationMock.getAddress().getStreetAndHouseNumber()).thenReturn(expectedStreetName);
    when(petrolStationMock.findPetrol(any())).thenReturn(Optional.ofNullable(petrolMock));

    // when
    messageContentStub.setMessage(petrolStationMock, PetrolType.E5);

    // then
    String expectedConcatenationResult = expectedHeadline
        + "\n" + expectedDistanceString
        + "\n\n" + expectedPetrolStationName + " - " + expectedOpenCloseString
        + "\n" + expectedStreetName;

    assertEquals(expectedConcatenationResult, messageContentStub.getMessage());
  }

  // Stub implements the abstract subject under test.
  private static class PetrolStationMessageContentStub extends PetrolStationMessageContent {
    private String title;
    private String message;

    @Override
    public String getTitle() {
      return this.title;
    }

    @Override
    public void setTitle(String title) {
      this.title = title;
    }

    @Override
    public String getMessage() {
      return this.message;
    }

    @Override
    public void setMessage(String text) {
      this.message = text;
    }

    @Override
    public void reset() {
    }
  }
}