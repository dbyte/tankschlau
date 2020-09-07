package de.fornalik.tankschlau.station;

import java.util.Objects;
import java.util.StringJoiner;

public class Petrol {
  public final PetrolType type;
  public final double price;

  public Petrol(PetrolType type, double price) {
    this.type = Objects.requireNonNull(type);
    this.price = price;
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
