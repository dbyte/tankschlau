package de.fornalik.tankschlau.util;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolType;

import java.awt.geom.Point2D;
import java.util.Optional;
import java.util.prefs.Preferences;

public class UserPreferences {
  private final Preferences realPrefs;

  public UserPreferences() {
    this.realPrefs = Preferences.userNodeForPackage(this.getClass());
  }

  public UserPreferences(Class<?> clazz) {
    this.realPrefs = Preferences.userNodeForPackage(clazz);
  }

  public Preferences getRealPrefs() {
    return realPrefs;
  }

  public Address readUserAddress() {
    Address address = new Address(
        realPrefs.get("address.name", ""),
        realPrefs.get("address.street", ""),
        realPrefs.get("address.houseNumber", ""),
        realPrefs.get("address.city", ""),
        realPrefs.get("address.postCode", ""),
        null);

    readUserGeo().ifPresent(address::setGeo);

    return address;
  }

  public void writeUserAddress(Address address) {
    realPrefs.put("address.name", address.getName());
    realPrefs.put("address.street", address.getStreet());
    realPrefs.put("address.houseNumber", address.getHouseNumber());
    realPrefs.put("address.city", address.getCity());
    realPrefs.put("address.postCode", address.getPostCode());
    address.getGeo().ifPresent(this::writeUserGeo);
  }

  public Optional<Geo> readUserGeo() {
    Optional<Point2D> latLon = readLatLon();
    if (!latLon.isPresent())
      return Optional.empty();

    double lat = latLon.get().getX();
    double lon = latLon.get().getY();

    double maybeDistance = realPrefs.getDouble("geo.distance", -9999.99);
    Double distance = maybeDistance != -9999.99 ? maybeDistance : null;

    Geo geo = new Geo(lat, lon, distance);

    return Optional.of(geo);
  }

  private Optional<Point2D> readLatLon() {
    double lat = realPrefs.getDouble("geo.latitude", -9999.99);
    double lon = realPrefs.getDouble("geo.longitude", -9999.99);

    if (lat == -9999.99 || lon == -9999.99)
      return Optional.empty();

    return Optional.of(new Point2D.Double(lat, lon));
  }

  public void writeUserGeo(Geo geo) {
    realPrefs.putDouble("geo.latitude", geo.getLatitude());
    realPrefs.putDouble("geo.longitude", geo.getLongitude());
    geo.getDistance().ifPresent(dist -> realPrefs.putDouble("geo.distance", dist));
  }

  public PetrolType readPreferredPetrolType() {
    String petrolTypeString = realPrefs.get("petrol.preferredType", PetrolType.E10.toString());
    return PetrolType.valueOf(PetrolType.class, petrolTypeString);
  }

  public void writePreferredPetrolType(PetrolType type) {
    realPrefs.put("petrol.preferredType", type.toString());
  }
}
