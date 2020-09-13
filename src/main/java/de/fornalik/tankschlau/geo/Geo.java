package de.fornalik.tankschlau.geo;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Geo {
  @SerializedName("lat") public final double latitude;
  @SerializedName("lng") public final double longitude;

  public Geo(double lat, double lon) {
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

    Geo that = (Geo) o;

    return new EqualsBuilder()
        .append(latitude, that.latitude)
        .append(longitude, that.longitude)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(23, 177)
        .append(latitude)
        .append(longitude)
        .toHashCode();
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
   *
   * @implNote Unchecked exception.
   * @see java.lang.RuntimeException
   */
  public static class InvalidCoordinatesException extends RuntimeException {
    public InvalidCoordinatesException(String message) {
      super(message);
    }
  }
}
