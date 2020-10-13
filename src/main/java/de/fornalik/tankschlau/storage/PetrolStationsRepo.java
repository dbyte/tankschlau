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
import de.fornalik.tankschlau.station.PetrolStation;

import java.util.List;

/**
 * Repo for petrol stations. Interacts with the storage through a service.
 */
public interface PetrolStationsRepo extends HasTransactionInfo {

  /**
   * @see PetrolStationsService#findAllInNeighbourhood(Geo)
   */
  List<PetrolStation> getNeighbourhoodStations(Geo geo);
}
