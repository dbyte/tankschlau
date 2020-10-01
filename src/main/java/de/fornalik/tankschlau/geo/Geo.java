/*
 * Copyright (c) 2020 Tammo Fornalik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fornalik.tankschlau.geo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.util.MyToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;
import java.util.Optional;

/**
 * Geographical data such as latitude, longitude, distance to a different {@link Geo} object etc.
 */
public class Geo {
  @SerializedName("lat") private final double latitude;
  @SerializedName("lng") private final double longitude;
  @SerializedName("dist") private Double distance;

  /**
   * Constructor
   *
   * @see Geo#Geo(double lat, double lon, Double distance)
   */
  public Geo(double lat, double lon) {
    this(lat, lon, null);
  }

  /**
   * Constructor
   *
   * @param lat      Latitude
   * @param lon      Longitude
   * @param distance Distance (km) of this geo location related to the user's address geo location.
   *                 This value is optional! Set to null if distance is unknown.
   */
  public Geo(double lat, double lon, Double distance) {
    this.latitude = lat;
    this.longitude = lon;

    throwOnInvalidCoordinates();
    setDistance(distance);
  }

  /**
   * Creates a new {@link Geo} by de-serializing a given {@link JsonObject}.
   *
   * @param in The {@link JsonObject} from which to convert to an {@link Geo}
   * @return Instance of {@link Geo}
   */
  public static Geo createFromJson(JsonObject in) {
    Objects.requireNonNull(in, "JsonObject must not be null.");

    return new Gson().fromJson(in, Geo.class);
  }

  /**
   * Get the road distance of this geo location related to the user's address geo location.
   *
   * @return Optional containing the road distance (km) to the user's address if present,
   * otherwise an empty Optional.
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
   */
  public void setDistance(Double km) {
    throwOnInvalidDistance(km);
    this.distance = km;
  }

  private void throwOnInvalidDistance(Double km) throws InvalidGeoDataException {
    /* null is perfectly valid here as it is always returned as an Optional by design
    and as a distance value is not mandatory by business rule. */
    if (km == null)
      return;

    if (km < 0.0)
      throw new InvalidGeoDataException("Geographical distance must be >= 0.0 km.");
  }

  private void throwOnInvalidCoordinates() throws InvalidGeoDataException {
    /* Throws InvalidGeoDataException if lat/lon are out of geographical constraints
    See also https://stackoverflow.com/a/47188298 */
    if (latitude < -85.05112878 || latitude > 85.05112878
        || longitude < -180.0 || longitude > 180.0)
      throw new InvalidGeoDataException(
          "One ore mode coordinates are out of bounds. Lat: " + latitude + ", Lon: " + longitude);
  }

  /**
   * @param o Object to compare to
   * @return True if values of latitude, longitude and distance are equal.
   */
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
    return new MyToStringBuilder(this)
        .append("latitude", latitude)
        .append("longitude", longitude)
        .append("distance", distance)
        .toString();
  }

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
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
