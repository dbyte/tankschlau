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
 * Repository interface for {@link PetrolStation} storage.
 */
public interface PetrolStationsRepo extends HasTransactionInfo {

  /**
   * Searches for petrol stations around the user's neighbourhood, whereby neighbourhood
   * is defined by the given {@link Geo} data of the user.
   *
   * @param geo {@link Geo} instance. Its latitude & longitude should reflect the current
   *            location of the user. Its distance field should reflect the maximum search radius
   *            for petrol stations in the neighbourhood of the users current location.
   * @return A list of {@link PetrolStation}, or an empty list if no stations were found.
   */
  List<PetrolStation> findAllInNeighbourhood(Geo geo);
}
