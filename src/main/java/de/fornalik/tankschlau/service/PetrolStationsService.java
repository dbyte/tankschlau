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


import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolStation;

import java.util.List;

/**
 * Service interface for petrol stations.
 */
public interface PetrolStationsService extends HasTransactionInfo {

  /**
   * @see PetrolStationsRepo#findAllInNeighbourhood(Geo)
   */
  List<PetrolStation> getNeighbourhoodStations(Geo geo);
}
