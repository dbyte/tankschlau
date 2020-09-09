package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Distance;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

public class PetrolStation {
  public final UUID uuid;
  public final String brand;
  public final boolean isOpen;
  public final Address address;
  private final Distance distance;
  private final HashSet<Petrol> petrols;

  public PetrolStation(
      UUID uuid,
      String brand,
      boolean isOpen,
      Address address,
      Distance distance,
      Set<Petrol> petrols) {

    this.uuid = Objects.requireNonNull(uuid);
    this.brand = Objects.requireNonNull(brand);
    this.isOpen = isOpen;
    this.address = Objects.requireNonNull(address);
    this.distance = distance;
    this.petrols = Optional.ofNullable((HashSet<Petrol>)petrols).orElse(new HashSet<>());
  }

  public Optional<Distance> getDistance() {
    return Optional.ofNullable(distance);
  }

  public HashSet<Petrol> getPetrols() {
    return petrols;
  }

  public Optional<Double> getPetrolPrice(PetrolType petrolType) {
    return Petrols
        .getPetrol(petrols, petrolType)
        .map(petrol -> petrol.price);
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("uuid", uuid)
        .append("brand", brand)
        .append("isOpen", isOpen)
        .append("address", address)
        .append("distance", distance)
        .append("petrols", petrols)
        .toString();
  }
}
