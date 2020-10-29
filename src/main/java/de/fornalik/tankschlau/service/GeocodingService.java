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

import java.util.Optional;

/**
 * Service interface for {@link Geo} model.
 */
public interface GeocodingService extends HasTransactionInfo {

  /**
   * Calls a webservice which delivers latitude/longitude for a given address and wraps it in
   * a {@link Geo} object.
   *
   * @param forAddress The address for which to retrieve lat/lng data.
   * @return Optional {@link Geo} object if the service has returned lat/lng.
   */
  Optional<Geo> findGeo(Address forAddress);
}
