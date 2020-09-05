package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Coordinates2D;
import de.fornalik.tankschlau.geo.Distance;
import de.fornalik.tankschlau.geo.GeoLocation;

import java.util.ArrayList;
import java.util.UUID;

public class PetrolStationBuilder {
  private ArrayList<Petrol> petrols = new ArrayList<>();
  private String name = "";
  private String brand = "";
  private String place = "";
  private GeoLocation geoLocation = new GeoLocation();
  private final Distance distance = new Distance(0.0);

  public PetrolStation create(UUID uuid) {
    return new PetrolStation(
        uuid,
        name,
        brand,
        place,
        geoLocation,
        distance,
        petrols);
  }

  public PetrolStationBuilder setBaseData(String name, String brand, String place) {
    this.name = name;
    this.brand = brand;
    this.place = place;

    return this;
  }

  public PetrolStationBuilder setPetrols(ArrayList<Petrol> petrols) {
    this.petrols = petrols;
    return this;
  }

  public PetrolStationBuilder addPetrol(PetrolType type, double price) {
    boolean doAppend = petrols.stream().noneMatch(p -> p.type.equals(type));
    if (doAppend)
      petrols.add(new Petrol(type, price));

    return this;
  }

  public PetrolStationBuilder setGeoLocation(GeoLocation geoLocation) {
    this.geoLocation = geoLocation;
    return this;
  }

  public PetrolStationBuilder setAddress(
      String street,
      String houseNumber,
      String postCode) {
    geoLocation.setStreet(street);
    geoLocation.setHouseNumber(houseNumber);
    geoLocation.setPostCode(postCode);

    return this;
  }

  public PetrolStationBuilder setCoordinates(double lat, double lon) {
    geoLocation.setCoordinates2D(new Coordinates2D(lat, lon));
    return this;
  }

  public PetrolStationBuilder setDistance(double km) {
    distance.setKm(km);
    return this;
  }
}
