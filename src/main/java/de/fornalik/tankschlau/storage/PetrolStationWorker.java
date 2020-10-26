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

package de.fornalik.tankschlau.storage;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStationBuilder;
import de.fornalik.tankschlau.util.RunnableCallbackWorker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Simulate some operations which should be logged into the Swing TextArea.
 */
public class PetrolStationWorker implements RunnableCallbackWorker<List<PetrolStation>> {
  public static final Logger LOGGER = Logger.getLogger(PetrolStationWorker.class.getName());
  private Consumer<List<PetrolStation>> callback;

  public PetrolStationWorker() {
    this.callback = null;
  }

  @Override
  public void setCallback(Consumer<List<PetrolStation>> callback) {
    this.callback = callback;
  }

  @Override
  public void run() {
    LOGGER.info("doing work 3 seconds");
    List<PetrolStation> data = new ArrayList<>();

    try {
      // int divisionByZero = 1 / 0;
      Thread.sleep(3000);
      data = createDemoData();
    }

    catch (InterruptedException e) {
      e.printStackTrace();
      LOGGER.severe("Interrupted: " + e.getMessage());
      return;
    }

    catch (Exception e) {
      e.printStackTrace();
      String errMsg = e.getMessage()
          + ". Trace: \n" + Arrays.toString(e.getStackTrace()).replace(", ", "\n")
          + "\n";

      LOGGER.severe(errMsg);

      // Interrupt to This does NOT invoke an InterruptedException as I'm interrupting myself,
      // which is always
      // permitted.
      Thread.currentThread().interrupt();
    }

    LOGGER.info("Work done.");

    callback.accept(data);
  }

  private List<PetrolStation> createDemoData() {
    List<PetrolStation> data = new ArrayList<>();

    Geo geo = new Geo(52.019283673, 48.1625243, 4.3);
    Address address = new Address(
        "ARAL am Westend",
        "Büllibällistraße",
        "41",
        "München",
        "80000",
        geo);
    PetrolStation station = PetrolStationBuilder.create(UUID.randomUUID())
        .withAddress(address)
        .withBrand("ARAL")
        .withIsOpen(true)
        .build();
    data.add(station);

    geo = new Geo(51.0191031, 49.1625243, 5.2);
    address = new Address(
        "ESSO Burgwall, Inh. Izmir Schlecht",
        "Burgwall",
        "113",
        "München",
        "80001",
        geo);
    station = PetrolStationBuilder.create(UUID.randomUUID())
        .withAddress(address)
        .withBrand("ESSO")
        .withIsOpen(false)
        .build();
    data.add(station);

    geo = new Geo(50.139461, 47.937462, 5.0);
    address = new Address("OMV Schlamp", "Dödelberghain", "12b", "München", "80002", geo);
    station = PetrolStationBuilder.create(UUID.randomUUID())
        .withAddress(address)
        .withBrand("OMV")
        .withIsOpen(false)
        .build();
    data.add(station);

    return data;
  }
}
