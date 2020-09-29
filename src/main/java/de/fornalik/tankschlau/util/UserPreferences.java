package de.fornalik.tankschlau.util;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolType;

import java.util.prefs.Preferences;

public class UserPreferences {
  private final Preferences prefs;

  public UserPreferences(Class<?> clazz) {
    this.prefs = Preferences.userNodeForPackage(clazz);
  }

  public UserPreferences() {
    this.prefs = Preferences.userNodeForPackage(this.getClass());
  }

  public void setUserAddress(Address address) {
    prefs.put("address.name", address.getName());
    prefs.put("address.street", address.getStreet());
    prefs.put("address.houseNumber", address.getHouseNumber());
    prefs.put("address.city", address.getCity());
    prefs.put("address.postCode", address.getPostCode());
    address.getGeo().ifPresent(this::setUserGeo);
  }

  public void setUserGeo(Geo geo) {
    prefs.putDouble("geo.latitude", geo.getLatitude());
    prefs.putDouble("geo.longitude", geo.getLongitude());
    geo.getDistance().ifPresent(dist -> prefs.putDouble("geo.distance", dist));
  }

  public void setPreferredPetrolType(PetrolType type) {
    prefs.put("petrol.preferredType", type.toString());
  }
}
