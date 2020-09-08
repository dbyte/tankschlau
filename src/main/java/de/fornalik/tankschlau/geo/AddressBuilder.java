package de.fornalik.tankschlau.geo;

import de.fornalik.tankschlau.util.StringLegalizer;

import java.util.HashMap;
import java.util.Map;

public class AddressBuilder {
  private final Map<String, String> mandatoryFields = new HashMap<>();
  private String name;
  private String houseNumber;
  private Coordinates2D coordinates2D;

  private AddressBuilder() {
  }

  public static AddressBuilder init() {
    return new AddressBuilder();
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

  public AddressBuilder setMandatoryFields(String street, String city, String postCode) {
    StringLegalizer.init(street).mandatory().end();
    StringLegalizer.init(city).mandatory().end();
    StringLegalizer.init(postCode).mandatory().end();

    mandatoryFields.put("street", street);
    mandatoryFields.put("city", city);
    mandatoryFields.put("postCode", postCode);

    return this;
  }

  public AddressBuilder setName(String name) {
    this.name = name;
    return this;
  }

  public AddressBuilder setHouseNumber(String houseNumber) {
    this.houseNumber = houseNumber;
    return this;
  }

  public AddressBuilder setCoordinates2D(Coordinates2D coords) {
    this.coordinates2D = coords;
    return this;
  }

  public AddressBuilder setCoordinates2D(double lat, double lon) {
    this.coordinates2D = new Coordinates2D(lat, lon);
    return this;
  }
}
