package de.fornalik.tankschlau.geo;

import java.util.StringJoiner;

public class Coordinates2D {
  private final double latitude;
  private final double longitude;

  public Coordinates2D(double lat, double lon) {
    this.latitude = lat;
    this.longitude = lon;
    throwOnInvalidCoordinates();
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  private void throwOnInvalidCoordinates() throws InvalidCoordinatesException {
    // Source: https://stackoverflow.com/a/47188298
    if (latitude < -85.05112878 || latitude > 85.05112878
        || longitude < -180.0 || longitude > 180.0)
      throw new InvalidCoordinatesException(
          "One ore mode coordinates are out of bounds. Lat: " + latitude + ", Lon: " + longitude);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Coordinates2D.class.getSimpleName() + "[", "]")
        .add("latitude=" + latitude)
        .add("longitude=" + longitude)
        .toString();
  }

  public static class InvalidCoordinatesException extends RuntimeException {
    public InvalidCoordinatesException(String message) {
      super(message);
    }
  }
}
