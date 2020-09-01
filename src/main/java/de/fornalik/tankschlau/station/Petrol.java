package de.fornalik.tankschlau.station;

import java.util.StringJoiner;

public class Petrol {
  public final PetrolType type;
  public final double price;

  public Petrol(PetrolType type, double price) {
    this.type = type;
    this.price = price;
  }

  /*public Map<PetrolType, Double> getAsMap() {
    Map<PetrolType, Double> map = new HashMap<>();
    map.put(type, price);
    return map;
  }*/

  @Override
  public String toString() {
    return new StringJoiner(", ", Petrol.class.getSimpleName() + "[", "]")
        .add("type=" + type)
        .add("price=" + price)
        .toString();
  }
}
