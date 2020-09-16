package de.fornalik.tankschlau.station;

import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.geo.Address;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

public class PetrolStation {
  private static final String MUST_NOT_BE_NULL = " must not be null";

  @SerializedName("id") public final UUID uuid;
  @SerializedName("brand") public final String brand;
  @SerializedName("isOpen") public final boolean isOpen;
  public final Address address;
  private final HashSet<Petrol> petrols;

  /**
   * Constructor
   *
   * @param uuid UUID of the station, given by API response - mandatory - not null
   * @param brand Brand - can be empty String - not null
   * @param isOpen Flags if station was open at API request time - default false
   * @param address Address and its geographical data - mandatory
   * @param petrols The station's petrol data - nullable
   */
  public PetrolStation(
      UUID uuid,
      String brand,
      boolean isOpen,
      Address address,
      Set<Petrol> petrols) {

    this.uuid = Objects.requireNonNull(uuid, "uuid" + MUST_NOT_BE_NULL);
    this.brand = Objects.requireNonNull(brand, "brand" + MUST_NOT_BE_NULL);
    this.isOpen = isOpen;
    this.address = Objects.requireNonNull(address, "address" + MUST_NOT_BE_NULL);
    this.petrols = Optional.ofNullable((HashSet<Petrol>) petrols).orElse(new HashSet<>());
  }

  /**
   * Get a Set of {@link Petrol} objects for this petrol station.
   * It's guaranteed that every station has a unique Set of petrols or an empty Set<br>
   * Cardinality is defined as 0...infinit.
   *
   * @return A Set of {@link Petrol} objects, else an empty Set.
   */
  public HashSet<Petrol> getPetrols() {
    if (petrols == null) return new HashSet<>();
    return petrols;
  }

  /**
   * Finds a {@link Petrol} object within this PetrolStation, searching by its {@link PetrolType}.
   *
   * @param type The {@link PetrolType} to search for.
   * @return An Optional when a {@link Petrol} object was found, else an empty Optional.
   */
  public Optional<Petrol> findPetrol(PetrolType type) {
    return getPetrols()
        .stream()
        .filter(p -> p.type == type)
        .findFirst();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("uuid", uuid)
        .append("brand", brand)
        .append("isOpen", isOpen)
        .append("address", address)
        .append("petrols", petrols)
        .toString();
  }
}
