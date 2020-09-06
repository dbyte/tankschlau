package de.fornalik.tankschlau.geo;

import java.util.StringJoiner;

public class Distance {
  private double km;

  public Distance(double km) {
    setKm(km);
  }

  public double getKm() {
    return km;
  }

  public void setKm(double km) throws IllegalArgumentException {
    if (km < 0.0) throw new IllegalArgumentException("Distance must be >= 0.0");
    this.km = km;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Distance that = (Distance) o;
    return this.km == that.km;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Distance.class.getSimpleName() + "[", "]")
        .add("km=" + km)
        .toString();
  }
}
