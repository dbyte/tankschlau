package de.fornalik.tankschlau.geo;

import de.fornalik.tankschlau.util.StringLegalizer;

import java.util.*;

public class Address {
  private String name;
  private String street;
  private String houseNumber;
  private String city;
  private String postCode;
  private Coordinates2D coordinates2D;

  public Address(String street, String city, String postCode) {
    this(null, street, null, city, postCode, null);
  }

  public Address(
      String name,
      String street,
      String houseNumber,
      String city,
      String postCode,
      Coordinates2D coordinates2D) {

    setName(name);
    setStreet(street);
    setHouseNumber(houseNumber);
    setCity(city);
    setPostCode(postCode);
    setCoordinates2D(coordinates2D);
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = StringLegalizer.init(name).nullToEmpty().safeTrim().end();
  }

  public String getStreet() {
    return street;
  }

  public void setStreet(String street) {
    this.street = StringLegalizer.init(street).mandatory().safeTrim().end();
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  public void setHouseNumber(String houseNumber) {
    this.houseNumber = StringLegalizer.init(houseNumber).nullToEmpty().safeTrim().end();
  }

  public String getCity() {
    return city;
  }

  public void setCity(String city) {
    this.city = StringLegalizer.init(city).mandatory().safeTrim().end();
  }

  public String getPostCode() {
    return postCode;
  }

  public void setPostCode(String postCode) {
    this.postCode = StringLegalizer.init(postCode).mandatory().safeTrim().end();
  }

  public Optional<Coordinates2D> getCoordinates2D() {
    return Optional.ofNullable(coordinates2D);
  }

  public void setCoordinates2D(Coordinates2D coordinates2D) {
    this.coordinates2D = coordinates2D;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Address that = (Address) o;
    return Objects.equals(getName(), that.getName()) &&
        Objects.equals(getStreet(), that.getStreet()) &&
        Objects.equals(getHouseNumber(), that.getHouseNumber()) &&
        Objects.equals(getCity(), that.getCity()) &&
        Objects.equals(getPostCode(), that.getPostCode()) &&
        Objects.equals(getCoordinates2D(), that.getCoordinates2D());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Address.class.getSimpleName() + "[", "]")
        .add("name='" + name + "'")
        .add("street='" + street + "'")
        .add("houseNumber='" + houseNumber + "'")
        .add("postCode='" + postCode + "'")
        .add("city='" + city + "'")
        .add("coordinates2D=" + getCoordinates2D())
        .toString();
  }

  public static class Builder {
    private final Map<String, String> mandatoryFields = new HashMap<>();
    private String name;
    private String houseNumber;
    private Coordinates2D coordinates2D;

    private Builder() {
    }

    public static Builder init() {
      return new Builder();
    }

    public Address build() {
      return new Address(
          name,
          mandatoryFields.get("street"),
          houseNumber,
          mandatoryFields.get("city"),
          mandatoryFields.get("postCode"),
          coordinates2D);
    }

    public Builder setMandatoryFields(String street, String city, String postCode) {
      StringLegalizer.init(street).mandatory().end();
      StringLegalizer.init(city).mandatory().end();
      StringLegalizer.init(postCode).mandatory().end();

      mandatoryFields.put("street", street);
      mandatoryFields.put("city", city);
      mandatoryFields.put("postCode", postCode);

      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setHouseNumber(String houseNumber) {
      this.houseNumber = houseNumber;
      return this;
    }

    public Builder setCoordinates2D(Coordinates2D coords) {
      this.coordinates2D = coords;
      return this;
    }

    public Builder setCoordinates2D(double lat, double lon) {
      this.coordinates2D = new Coordinates2D(lat, lon);
      return this;
    }
  }
}
