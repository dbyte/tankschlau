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

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.util.RunnableCallbackWorker;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Simulate some operations which should be logged into the Swing TextArea.
 */
public class GeocodingWorker implements RunnableCallbackWorker<Geo> {
  public static final Logger LOGGER = Logger.getLogger(GeocodingWorker.class.getName());
  private Consumer<Geo> callback;

  public GeocodingWorker() {
    this.callback = null;
  }

  @Override
  public void setCallback(Consumer<Geo> callback) {
    this.callback = callback;
  }

  @Override
  public void run() {
    LOGGER.info("Requesting Geocoding data");
    Geo data = null;

    try {
      // int divisionByZero = 1 / 0;
      Thread.sleep(2000);
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

  private Geo createDemoData() {
    return new Geo(52.983041830, 48.19385643, 4.123);
  }
}