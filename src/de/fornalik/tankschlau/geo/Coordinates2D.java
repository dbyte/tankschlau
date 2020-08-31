package de.fornalik.tankschlau.geo;

import java.util.StringJoiner;

public class Coordinates2D {
  private final double latitude;
  private final double longitude;

  public Coordinates2D(double lat, double lon) {
    this.latitude = lat;
    this.longitude = lon;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Coordinates2D.class.getSimpleName() + "[", "]")
        .add("latitude=" + latitude)
        .add("longitude=" + longitude)
        .toString();
  }
}
