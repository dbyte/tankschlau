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
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.storage.PetrolStationsDao;
import de.fornalik.tankschlau.storage.PetrolStationsService;
import de.fornalik.tankschlau.storage.TransactInfo;

import java.util.List;
import java.util.Objects;

/**
 * This is the highest abstraction of tankerkoenig.de webservice. It represents its
 * Service Layer. Use this one inside a View Model or the Presentation Layer.
 */
public class PetrolStationsWebService implements PetrolStationsService {
  private final PetrolStationsDao dao;

  public PetrolStationsWebService(PetrolStationsDao dao) {
    this.dao = Objects.requireNonNull(dao);
  }

  /**
   * Searches for petrol stations around the user's neighbourhood, whereby neighbourhood is
   * defined by the given Geo data of the user.
   *
   * @see PetrolStationsDao#findAllInNeighbourhood(Geo)
   */
  @Override
  public List<PetrolStation> getNeighbourhoodStations(Geo geo) {
    return dao.findAllInNeighbourhood(geo);
  }

  @Override
  public TransactInfo getTransactInfo() {
    return dao.getTransactInfo();
  }
}
