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
import de.fornalik.tankschlau.net.BaseRequest;

/**
 * Abstract HTTP request class with base functionalities and <b>additional hook</b> for generating
 * proper URL request parameters for an {@link Address}.
 */
public abstract class AddressRequest extends BaseRequest {

  /**
   * Implementation should set or overwrite the address URL parameters for this request.
   *
   * @param address The {@link Address} data. Its {@link de.fornalik.tankschlau.geo.Geo}
   *                object should be updated with the resulting latitude & longitude by calling a
   *                geocoding webservice.
   * @throws NullPointerException If given address is null.
   */
  public abstract void setAddressUrlParameters(Address address);
}
