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

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolType;
import de.fornalik.tankschlau.station.testhelp.PetrolStationTestHelper;
import de.fornalik.tankschlau.util.Localization;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PetrolStationMessageContentTest {
  PetrolStationTestHelper domainHelp;
  private PetrolStationMessageContent messageContent; // SUT
  private Localization l10nMock;
  private PetrolStation petrolStationMock;
  private PetrolType petrolTypeE5;
  private Address addressMock;
  private Geo geoMock;

  @BeforeEach
  void setUp() {
    domainHelp = new PetrolStationTestHelper();
    l10nMock = mock(Localization.class);
    petrolStationMock = mock(PetrolStation.class, Mockito.RETURNS_DEEP_STUBS);
    petrolTypeE5 = PetrolType.E5;
    addressMock = mock(Address.class);
    geoMock = mock(Geo.class);

    messageContent = mock(PetrolStationMessageContent.class, Mockito.CALLS_REAL_METHODS);
  }

  @Test
  void setMessage_() {
    // given
    when(petrolStationMock.address.getName()).thenReturn("Some station name");

    // when
    messageContent.setMessage(petrolStationMock, petrolTypeE5);

    // then

  }
}