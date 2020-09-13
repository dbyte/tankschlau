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

  public PetrolStationBuilder setBrand(String brand) {
    this.brand = Objects.requireNonNull(brand, "brand" + MUST_NOT_BE_NULL);

    return this;
  }

  public PetrolStationBuilder setIsOpen(boolean isOpen) {
    this.isOpen = isOpen;
    return this;
  }

  public PetrolStationBuilder setPetrols(Set<Petrol> petrols) {
    this.petrols = Objects.requireNonNull((HashSet<Petrol>) petrols, "petrol" + MUST_NOT_BE_NULL);
    return this;
  }

  /**
   * Adds {@link Petrol} with its price to a {@link PetrolStation} through this builder.
   * <br><br>
   * Note: Adding the same {@link PetrolType} multiple times does nothing, as it's compared by
   * implicitly calling {@link Petrol#equals(Object)}.
   *
   * @param type  The {@link PetrolType} of {@link Petrol} to add
   * @param price The price of the given {@link PetrolType}
   * @return {@link PetrolStationBuilder} to fulfill the builder pattern
   */
  public PetrolStationBuilder addPetrol(PetrolType type, double price) {
    Objects.requireNonNull(type, "type" + MUST_NOT_BE_NULL);

    petrols.add(new Petrol(type, price));
    return this;
  }

  public PetrolStationBuilder setAddress(Address address) {
    this.address = Objects.requireNonNull(address, "address" + MUST_NOT_BE_NULL);
    return this;
  }
}
