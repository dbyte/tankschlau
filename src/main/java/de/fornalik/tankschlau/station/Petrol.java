package de.fornalik.tankschlau.station;

import java.util.StringJoiner;

public class Petrol implements Comparable<Petrol> {
  public final PetrolType type;
  public final double price;

  public Petrol(PetrolType type, double price) {
    this.type = type;
    this.price = price;
  }

  /**
   * Use for sorting a Petrol array, based on enum {@link PetrolType}.
   * The array will get sorted based on the order in which the enum values are defined.
   *
   * @param other The other {@link Petrol} object to which we want compare
   * @return The ordinal difference between the {@link PetrolType}s of 2 {@link Petrol} objects.
   */
  @Override
  public int compareTo(Petrol other) {
    try {
      return type.compareTo(other.type);
    } catch (NullPointerException | ClassCastException e) {
      return 0;
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Petrol that = (Petrol) o;
    return this.type == that.type && this.price == that.price;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Petrol.class.getSimpleName() + "[", "]")
        .add("type=" + type)
        .add("price=" + price)
        .toString();
  }
}
