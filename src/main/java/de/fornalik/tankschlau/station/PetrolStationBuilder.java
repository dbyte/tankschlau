package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Coordinates2D;
import de.fornalik.tankschlau.geo.Distance;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.util.LocaleStrings;
import de.fornalik.tankschlau.util.StringLegalizer;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class PetrolStationBuilder {
  private Distance distance;
  private ArrayList<Petrol> petrols = new ArrayList<>();
  private String brand = "";
  private Address address;

  public PetrolStation build(UUID uuid) {
    return new PetrolStation(
        uuid,
        brand,
        address,
        distance,
        petrols);
  }

  public PetrolStationBuilder setBrand(String brand) {
    this.brand = Objects.requireNonNull(brand, LocaleStrings.mustNotBeNull("brand"));

    return this;
  }

  public PetrolStationBuilder setPetrols(ArrayList<Petrol> petrols) {
    this.petrols = Objects.requireNonNull(petrols, LocaleStrings.mustNotBeNull("petrols"));
    return this;
  }

  public PetrolStationBuilder addPetrol(PetrolType type, double price) {
    Objects.requireNonNull(type, "type must not be null.");

    boolean isDuplicate = petrols.stream().anyMatch(other -> other.type.equals(type));
    if (isDuplicate) return this;

    petrols.add(new Petrol(type, price));
    return this;
  }

  public PetrolStationBuilder setAddress(Address address) {
    this.address = Objects.requireNonNull(
        address, LocaleStrings.mustNotBeNull("address"));
    return this;
  }

  public PetrolStationBuilder setAddress(
      String street,
      String houseNumber,
      String postCode) throws StringLegalizer.ValueException {

    address.setStreet(street);
    address.setHouseNumber(houseNumber);
    address.setPostCode(postCode);

    return this;
  }

  public PetrolStationBuilder setCoordinates(double lat, double lon) {
    address.setCoordinates2D(new Coordinates2D(lat, lon));
    return this;
  }

  public PetrolStationBuilder setDistance(double km) {
    distance = new Distance(km);
    return this;
  }
}
