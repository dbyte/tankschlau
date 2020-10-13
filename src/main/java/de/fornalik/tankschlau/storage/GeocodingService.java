package de.fornalik.tankschlau.storage;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;

import java.util.Optional;

/**
 * Service interface for {@link Geo} model.
 */
public interface GeocodingService {

  /**
   * Calls a webservice which delivers latitude/longitude for a given address and wraps it in
   * a {@link Geo} object.
   *
   * @param forAddress The address for which to retrieve lat/lng data.
   * @return Optional {@link Geo} object if the service has returned lat/lng.
   */
  Optional<Geo> findGeo(Address forAddress);

  /**
   * @return Some valuable information about the last transaction with the storage.
   * @see TransactInfo
   */
  TransactInfo getTransactInfo();
}
