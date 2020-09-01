package de.fornalik.tankschlau.geo;

import java.util.StringJoiner;

public class Coordinates2D {
  public final double LATITUDE;
  public final double LONGITUDE;

  public Coordinates2D(double lat, double lon) {
    this.LATITUDE = lat;
    this.LONGITUDE = lon;
    throwOnInvalidCoordinates();
  }

  private void throwOnInvalidCoordinates() throws InvalidCoordinatesException {
    // Source: https://stackoverflow.com/a/47188298
    if (LATITUDE < -85.05112878 || LATITUDE > 85.05112878
        || LONGITUDE < -180.0 || LONGITUDE > 180.0)
      throw new InvalidCoordinatesException(
          "One ore mode coordinates are out of bounds. Lat: " + LATITUDE + ", Lon: " + LONGITUDE);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Coordinates2D.class.getSimpleName() + "[", "]")
        .add("latitude=" + LATITUDE)
        .add("longitude=" + LONGITUDE)
        .toString();
  }

  public static class InvalidCoordinatesException extends RuntimeException {
    public InvalidCoordinatesException(String message) {
      super(message);
    }
  }
}
