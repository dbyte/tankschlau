package de.fornalik.tankschlau.station;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Objects;

public class Petrol {
  public final PetrolType type;
  public final double price;

  public Petrol(PetrolType type, double price) {
    this.type = Objects.requireNonNull(type, "type must not be null.");
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Petrol petrol = (Petrol) o;

    return new EqualsBuilder()
        .append(price, petrol.price)
        .append(type, petrol.type)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
        .append(type)
        .append(price)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("type", type)
        .append("price", price)
        .toString();
  }


}
