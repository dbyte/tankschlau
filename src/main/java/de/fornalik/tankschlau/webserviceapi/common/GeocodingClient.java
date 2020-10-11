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
import de.fornalik.tankschlau.net.Response;

import java.io.IOException;
import java.util.Optional;

/**
 * Interface for geocoding webservices.
 *
 * @param <ResponseType> The type of the {@link Response}.
 */
public interface GeocodingClient<ResponseType> {

  /**
   * Implementation should call a geocoding webservice with the provided {@link Address} data,
   * getting back latitude & longitude, wrapped in a {@link Geo} instance.
   *
   * @param address The Address which lacks of latitude & longitude.
   * @return Latitude & longitude data, wrapped in a {@link Geo} instance on successful
   * interaction with the geocoding service, else an empty Optional.
   * @throws IOException If something went wrong while reading the response of the webservice or
   *                     while requesting the server.
   */
  Optional<Geo> getGeo(Address address) throws IOException;

  /**
   * @return Licence string.
   * @implSpec Implement according to provider's terms of use!
   */
  String getLicenseString();

  /**
   * @return The {@link Response} of the last request.
   */
  ResponseType getResponse();
}
