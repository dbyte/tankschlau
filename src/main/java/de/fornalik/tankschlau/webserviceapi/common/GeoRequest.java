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
import de.fornalik.tankschlau.net.BaseRequest;

/**
 * Abstract HTTP request class with base functionalities and <b>additional hook</b> for generating
 * proper URL request parameters for {@link Geo} data.
 */
public abstract class GeoRequest extends BaseRequest {

  /**
   * Implementation should set or overwrite the latitude/longitude/distance URL parameters
   * for this request.
   *
   * @param geo The {@link Geo} data.
   * @throws NullPointerException If given Geo instance is null.
   */
  public abstract void setGeoUrlParameters(Geo geo);
}