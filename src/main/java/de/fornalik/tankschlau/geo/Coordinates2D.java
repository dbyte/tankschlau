package de.fornalik.tankschlau.geo;

import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.StringJoiner;

public class Coordinates2D {
  public final double latitude;
  public final double longitude;

  public Coordinates2D(double lat, double lon) {
    this.latitude = lat;
    this.longitude = lon;
    throwOnInvalidCoordinates();
  }

  private void throwOnInvalidCoordinates() throws InvalidCoordinatesException {
    // Source: https://stackoverflow.com/a/47188298
    if (latitude < -85.05112878 || latitude > 85.05112878
        || longitude < -180.0 || longitude > 180.0)
      throw new InvalidCoordinatesException(
          "One ore mode coordinates are out of bounds. Lat: " + latitude + ", Lon: " + longitude);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Coordinates2D that = (Coordinates2D) o;

    if (Double.compare(that.latitude, latitude) != 0) return false;
    return Double.compare(that.longitude, longitude) == 0;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("latitude", latitude)
        .append("longitude", longitude)
        .toString();
  }

  /**
   * Exception thrown when latitude, longitude or both are outside the valid range.
   * @implNote Unchecked exception.
   * @see java.lang.RuntimeException
   */
  public static class InvalidCoordinatesException extends RuntimeException {
    public InvalidCoordinatesException(String message) {
      super(message);
    }
  }
}
