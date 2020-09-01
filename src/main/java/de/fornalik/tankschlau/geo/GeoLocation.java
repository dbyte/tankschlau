package de.fornalik.tankschlau.geo;

import java.util.StringJoiner;

public class GeoLocation {
  private String street;
  private String houseNumber;
  private String postCode;
  private Coordinates2D coordinates2D;

  public GeoLocation(Coordinates2D coordinates2D) {
    this.coordinates2D = coordinates2D;
  }

  public GeoLocation(String street, String houseNumber, String postCode) {
    this.street = street;
    this.houseNumber = houseNumber;
    this.postCode = postCode;
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

  public Coordinates2D getCoordinates2D() throws CoordinatesNullException {
    if (coordinates2D == null) throw new CoordinatesNullException(this);
    return coordinates2D;
  }

  public void setCoordinates2D(Coordinates2D coordinates2D) {
    this.coordinates2D = coordinates2D;
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

  public static class CoordinatesNullException extends Exception {
    public CoordinatesNullException(GeoLocation geoLocation) {
      super("Coordinates are null for " + geoLocation.toString());
    }
  }
}
