package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Address;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class PetrolStationBuilder {
  private static final String MUST_NOT_BE_NULL = " must not be null";

  private final UUID uuid;
  private Set<Petrol> petrols = new HashSet<>();
  private String brand;
  private boolean isOpen;
  private Address address;

  private PetrolStationBuilder(UUID uuid) {
    this.uuid = Objects.requireNonNull(uuid);
  }

  public static PetrolStationBuilder create(UUID uuid) {
    return new PetrolStationBuilder(uuid);
  }

  public PetrolStation build() {
    return new PetrolStation(
        uuid,
        brand,
        isOpen,
        address,
        petrols);
  }

  public PetrolStationBuilder withBrand(String brand) {
    this.brand = Objects.requireNonNull(brand, "brand" + MUST_NOT_BE_NULL);
    return this;
  }

  public PetrolStationBuilder withIsOpen(boolean isOpen) {
    this.isOpen = isOpen;
    return this;
  }

  public PetrolStationBuilder withPetrols(Set<Petrol> petrols) {
    this.petrols = Objects.requireNonNull((HashSet<Petrol>) petrols, "petrol" + MUST_NOT_BE_NULL);
    return this;
  }

  public PetrolStationBuilder withAddress(Address address) {
    this.address = Objects.requireNonNull(address, "address" + MUST_NOT_BE_NULL);
    return this;
  }
}
