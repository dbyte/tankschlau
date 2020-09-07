package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Distance;

import java.util.*;

public class PetrolStation {
  public final UUID uuid;
  public final String brand;
  public final Address address;
  public final Distance distance;
  private final ArrayList<Petrol> petrols;

  public PetrolStation(
      UUID uuid,
      String brand,
      Address address,
      Distance distance,
      ArrayList<Petrol> petrols) {

    this.uuid = Objects.requireNonNull(uuid);
    this.brand = Objects.requireNonNull(brand);
    this.address = Objects.requireNonNull(address);
    this.distance = distance;
    this.petrols = Optional.ofNullable(petrols).orElse(new ArrayList<>());
  }

  public Optional<Distance> getDistance() {
    return Optional.ofNullable(distance);
  }

  public ArrayList<Petrol> getPetrols() {
    return petrols;
  }

  public Optional<Double> getPetrolPrice(PetrolType petrolType) {
    return Petrols
        .getPetrol(petrols, petrolType)
        .map(petrol -> petrol.price);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PetrolStation that = (PetrolStation) o;
    boolean isEqual = Objects.equals(uuid, that.uuid) &&
        Objects.equals(brand, that.brand) &&
        Objects.equals(address, that.address) &&
        Objects.equals(distance, that.distance);
    if (!isEqual) return false;

    // More expensive check, thus at the end.
    Petrols.sortByType(this.getPetrols());
    Petrols.sortByType(that.getPetrols());
    return Arrays.equals(getPetrols().toArray(), that.getPetrols().toArray());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", PetrolStation.class.getSimpleName() + "[", "]")
        .add("uuid=" + uuid)
        .add("brand='" + brand + "'")
        .add("address=" + address)
        .add("distance=" + distance)
        .add("petrols=" + petrols)
        .toString();
  }
}
