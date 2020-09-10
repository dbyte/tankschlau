package de.fornalik.tankschlau.geo;

import de.fornalik.tankschlau.util.StringLegalizer;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Optional;

public class Address {
  private String name;
  private String street;
  private String houseNumber;
  private String city;
  private String postCode;
  private Coordinates2D coordinates2D;

  public Address(String street, String city, String postCode) {
    this(street, city, postCode, null);
  }

  public Address(String street, String city, String postCode, Coordinates2D coordinates) {
    this("", street, "", city, postCode, coordinates);
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
    this.street = StringLegalizer.init(street).safeTrim().mandatory().end();
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
    this.city = StringLegalizer.init(city).safeTrim().mandatory().end();
  }

  public String getPostCode() {
    return postCode;
  }

  public void setPostCode(String postCode) {
    this.postCode = StringLegalizer.init(postCode).safeTrim().mandatory().end();
  }

  public Optional<Coordinates2D> getCoordinates2D() {
    return Optional.ofNullable(coordinates2D);
  }

  public void setCoordinates2D(Coordinates2D coordinates2D) {
    this.coordinates2D = coordinates2D;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("name", name)
        .append("street", street)
        .append("houseNumber", houseNumber)
        .append("city", city)
        .append("postCode", postCode)
        .append("coordinates2D", coordinates2D)
        .toString();
  }

}
