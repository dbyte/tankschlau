package de.fornalik.tankschlau.util;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolType;

import java.util.Optional;
import java.util.prefs.Preferences;

public class UserPreferences {
  private final Preferences javaPref;

  public UserPreferences() {
    this.javaPref = Preferences.userNodeForPackage(this.getClass());
  }

  public UserPreferences(Class<?> clazz) {
    this.javaPref = Preferences.userNodeForPackage(clazz);
  }

  public Preferences getJavaPref() {
    return javaPref;
  }

  public Address readUserAddress() {
    Address address = new Address(
        javaPref.get("address.name", ""),
        javaPref.get("address.street", ""),
        javaPref.get("address.houseNumber", ""),
        javaPref.get("address.city", ""),
        javaPref.get("address.postCode", ""),
        null);

    readUserGeo().ifPresent(address::setGeo);

    return address;
  }

  public void writeUserAddress(Address address) {
    javaPref.put("address.name", address.getName());
    javaPref.put("address.street", address.getStreet());
    javaPref.put("address.houseNumber", address.getHouseNumber());
    javaPref.put("address.city", address.getCity());
    javaPref.put("address.postCode", address.getPostCode());
    address.getGeo().ifPresent(this::writeUserGeo);
  }

  public Optional<Geo> readUserGeo() {
    double lat = javaPref.getDouble("geo.latitude", -9999.99);
    double lon = javaPref.getDouble("geo.longitude", -9999.99);
    if (lat == -9999.99 || lon == -9999.99)
      return Optional.empty();

    Double distance = javaPref.getDouble("geo.distance", -9999.99);
    distance = distance != -9999.99 ? distance : null;

    Geo geo = new Geo(lat, lon, distance);

    return Optional.of(geo);
  }

  public void writeUserGeo(Geo geo) {
    javaPref.putDouble("geo.latitude", geo.getLatitude());
    javaPref.putDouble("geo.longitude", geo.getLongitude());
    geo.getDistance().ifPresent(dist -> javaPref.putDouble("geo.distance", dist));
  }

  public PetrolType readPreferredPetrolType() {
    String petrolTypeString = javaPref.get("petrol.preferredType", PetrolType.E10.toString());
    return PetrolType.valueOf(PetrolType.class, petrolTypeString);
  }

  public void writePreferredPetrolType(PetrolType type) {
    javaPref.put("petrol.preferredType", type.toString());
  }
}
