package de.fornalik.tankschlau.geo;

import java.util.Objects;
import java.util.StringJoiner;

public class GeoLocation {
  private String street;
  private String houseNumber;
  private String postCode;
  private Coordinates2D coordinates2D;

  public GeoLocation() {
    this("",
         "",
         "",
         new Coordinates2D(0.0, 0.0));
  }

  public GeoLocation(
      String street,
      String houseNumber,
      String postCode,
      Coordinates2D coordinates2D) {
    setStreet(street);
    setHouseNumber(houseNumber);
    setPostCode(postCode);
    setCoordinates2D(coordinates2D);
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = street.trim();
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public void setHouseNumber(String houseNumber) {
    this.houseNumber = houseNumber.trim();
  }

  public String getPostCode() {
    return postCode;
  }

  public void setPostCode(String postCode) {
    this.postCode = postCode.trim();
  }

  public Coordinates2D getCoordinates2D() {
    return coordinates2D;
  }

  public void setCoordinates2D(final Coordinates2D coordinates2D) {
    this.coordinates2D = coordinates2D != null ? coordinates2D : new Coordinates2D(0, 0);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    GeoLocation that = (GeoLocation) o;
    return Objects.equals(getStreet(), that.getStreet()) &&
        Objects.equals(getHouseNumber(), that.getHouseNumber()) &&
        Objects.equals(getPostCode(), that.getPostCode()) &&
        Objects.equals(getCoordinates2D(), that.getCoordinates2D());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getStreet(), getHouseNumber(), getPostCode(), getCoordinates2D());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", GeoLocation.class.getSimpleName() + "[", "]")
        .add("street='" + street + "'")
        .add("houseNumber='" + houseNumber + "'")
        .add("postCode='" + postCode + "'")
        .add("coordinates2D=" + coordinates2D)
        .toString();
  }
}
