package de.fornalik.tankschlau.geo;

import de.fornalik.tankschlau.util.StringLegalizer;

import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

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
}
