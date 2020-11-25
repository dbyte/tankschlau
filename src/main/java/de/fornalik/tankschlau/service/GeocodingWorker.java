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

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.RunnableCallbackWorker;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.logging.Logger;

// TODO unit tests

/**
 * Worker for a Geocoding Webservice
 */
public class GeocodingWorker implements RunnableCallbackWorker<Geo> {

  private static final Logger LOGGER = Logger.getLogger(GeocodingWorker.class.getName());
  private static final Localization L10N = Localization.getInstance();
  private final GeocodingService geocodingService;
  private Address userAddress;
  private Consumer<Geo> callback;

  public GeocodingWorker(GeocodingService geocodingService) {
    this.geocodingService = Objects.requireNonNull(geocodingService);
    this.callback = null;
  }

  public GeocodingService getGeocodingService() {
    return geocodingService;
  }

  public void setUserAddress(Address userAddress) {
    this.userAddress = Objects.requireNonNull(userAddress);
  }

  @Override
  public void setCallback(Consumer<Geo> callback) {
    this.callback = callback;
  }

  @Override
  public void run() {
    LOGGER.info(L10N.get("msg.GeocodingRequestRunning"));
    Geo data = null;

    try {
      data = findUserGeo();
    }

    catch (Exception e) {
      e.printStackTrace();
      String errMsg = e.getMessage()
          + ". Trace: \n" + Arrays.toString(e.getStackTrace()).replace(", ", "\n")
          + "\n";

      LOGGER.severe(errMsg);

      // Interrupting my own thread does NOT invoke an InterruptedException and is always permitted.
      Thread.currentThread().interrupt();
    }

    LOGGER.info(L10N.get("msg.GeocodingRequestDone"));

    callback.accept(data);
  }

  private Geo findUserGeo() {
    Optional<Geo> geo = geocodingService.findGeo(userAddress);
    Optional<String> responseErrorMsg = geocodingService.getTransactInfo().getErrorMessage();

    if (responseErrorMsg.isPresent()) {
      LOGGER.warning(L10N.get("msg.NoGeocodingResultsForAddress", responseErrorMsg.get()));
      return null;
    }
    else if (!geo.isPresent()) {
      LOGGER.warning(L10N.get("msg.NoGeocodingResultsForAddress", ""));
      return null;
    }

    return geo.get();
  }
}
