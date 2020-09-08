package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Distance;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class PetrolStationBuilder {
  private static final String MUST_NOT_BE_NULL = " must not be null";

  private Distance distance;
  private ArrayList<Petrol> petrols = new ArrayList<>();
  private String brand;
  private boolean isOpen;
  private Address address;

  private PetrolStationBuilder() {
  }

  public static PetrolStationBuilder init() {
    return new PetrolStationBuilder();
  }

  public PetrolStation build(UUID uuid) {
    return new PetrolStation(
        uuid,
        brand,
        isOpen,
        address,
        distance,
        petrols);
  }

  public PetrolStationBuilder setBrand(String brand) {
    this.brand = Objects.requireNonNull(brand, "brand" + MUST_NOT_BE_NULL);

    return this;
  }

  public PetrolStationBuilder setIsOpen(boolean isOpen) {
    this.isOpen = isOpen;
    return this;
  }

  public PetrolStationBuilder setPetrols(ArrayList<Petrol> petrols) {
    this.petrols = Objects.requireNonNull(petrols, "petrol" + MUST_NOT_BE_NULL);
    return this;
  }

  public PetrolStationBuilder addPetrol(PetrolType type, double price) {
    Objects.requireNonNull(type, "type" + MUST_NOT_BE_NULL);

    boolean isDuplicate = petrols.stream().anyMatch(other -> other.type.equals(type));
    if (isDuplicate) return this;

    petrols.add(new Petrol(type, price));
    return this;
  }

  public PetrolStationBuilder setAddress(Address address) {
    this.address = Objects.requireNonNull(
        address, "address" + MUST_NOT_BE_NULL);
    return this;
  }

  public PetrolStationBuilder setDistance(double km) {
    distance = new Distance(km);
    return this;
  }
}
