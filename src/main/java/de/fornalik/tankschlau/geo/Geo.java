package de.fornalik.tankschlau.geo;

import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Optional;

public class Geo {
  @SerializedName("lat") public final double latitude;
  @SerializedName("lng") public final double longitude;
  @SerializedName("dist") private Double distance;

  public Geo(double lat, double lon) {
    this(lat, lon, null);
  }

  public Geo(double lat, double lon, Double distance) {
    this.latitude = lat;
    this.longitude = lon;

    throwOnInvalidCoordinates();
    setDistance(distance);
  }

  /**
   * Get the road distance of this geo location related to the user's address geo location.
   *
   * @return Optional containing the road distance to the user's address if present, otherwise an
   * empty Optional.
   */
  public Optional<Double> getDistance() {
    return Optional.ofNullable(distance);
  }

  /**
   * Set the distance of this geo location related to the user's address.
   * A value of 0.0 is permitted as the object may be very close. <b>DO NOT</b> set 0.0 when no
   * reliable distance can be provided - instead
   * <br><b>set null if there is no user address or if we weren't able to calc his geo data.</>
   *
   * @param km Distance to User's address or null
   * @throws IllegalArgumentException if an invalid distance was given
   */
  public void setDistance(Double km) throws InvalidGeoDataException {
    // null is permitted here as it is always returned as an Optional by design.
    if (km != null && km < 0.0) throw new InvalidGeoDataException("Distance must be >= 0.0");
    this.distance = km;
  }

  private void throwOnInvalidCoordinates() throws InvalidGeoDataException {
    // Source: https://stackoverflow.com/a/47188298
    if (latitude < -85.05112878 || latitude > 85.05112878
        || longitude < -180.0 || longitude > 180.0)
      throw new InvalidGeoDataException(
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
        .append(distance, that.distance)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(23, 177)
        .append(latitude)
        .append(longitude)
        .append(distance)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("latitude", latitude)
        .append("longitude", longitude)
        .append("distance", distance)
        .toString();
  }

  /**
   * Exception thrown when latitude, longitude, distance or all are outside the valid range.
   *
   * @implNote Unchecked exception.
   * @see java.lang.RuntimeException
   */
  public static class InvalidGeoDataException extends RuntimeException {
    public InvalidGeoDataException(String message) {
      super(message);
    }
  }
}
