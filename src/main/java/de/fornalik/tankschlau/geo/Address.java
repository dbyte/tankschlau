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

import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.util.MyToStringBuilder;
import de.fornalik.tankschlau.util.StringLegalizer;

import java.util.Optional;

public class Address {
  @SerializedName("name") private String name;
  @SerializedName("street") private String street;
  @SerializedName("houseNumber") private String houseNumber;
  @SerializedName("place") private String city;
  @SerializedName("postCode") private String postCode;
  private Geo geo;

  /**
   * Constructor
   *
   * @see #Address(String name, String street, String houseNumber, String city, String postCode, Geo geo)
   */
  public Address(String street, String city, String postCode) {
    this(street, "", city, postCode);
  }

  /**
   * Constructor
   *
   * @see #Address(String name, String street, String houseNumber, String city, String postCode, Geo geo)
   */
  public Address(String street, String city, String postCode, Geo geo) {
    this("", street, "", city, postCode, geo);
  }

  /**
   * Constructor
   *
   * @see #Address(String name, String street, String houseNumber, String city, String postCode, Geo geo)
   */
  public Address(String street, String houseNumber, String city, String postCode) {
    this("", street, houseNumber, city, postCode, null);
  }

  /**
   * Constructor
   *
   * @param name        Name of person or company, may be empty
   * @param street      Street (excluding house number), mandatory
   * @param houseNumber House number, may be empty
   * @param city        City or place, mandatory
   * @param postCode    Post code, mandatory
   * @param geo         Additional geo data. Null is permitted if no data are available
   */
  public Address(
      String name,
      String street,
      String houseNumber,
      String city,
      String postCode,
      Geo geo) {

    setName(name);
    setStreet(street);
    setHouseNumber(houseNumber);
    setCity(city);
    setPostCode(postCode);
    setGeo(geo);
  }

  public String getName() {
    return name;
  }

  /**
   * @param name Sets name of a person or company. Passing in null leads to an empty String.
   */
  public void setName(String name) {
    this.name = StringLegalizer.create(name).nullToEmpty().safeTrim().end();
  }

  public String getStreet() {
    return street;
  }

  /**
   * @param street Sets street (excluding house number).
   * @throws StringLegalizer.ValueException if we passed null or an empty String
   */
  public void setStreet(String street) {
    this.street = StringLegalizer.create(street).safeTrim().mandatory().end();
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  /**
   * @param houseNumber Sets house number. Passing in null leads an empty String.
   */
  public void setHouseNumber(String houseNumber) {
    this.houseNumber = StringLegalizer.create(houseNumber).nullToEmpty().safeTrim().end();
  }

  public String getCity() {
    return city;
  }

  /**
   * @param city Sets city or place
   * @throws StringLegalizer.ValueException if we passed null or an empty String
   */
  public void setCity(String city) {
    this.city = StringLegalizer.create(city).safeTrim().mandatory().end();
  }

  public String getPostCode() {
    return postCode;
  }

  /**
   * @param postCode Sets post code
   * @throws StringLegalizer.ValueException if we passed null or an empty String
   */
  public void setPostCode(String postCode) {
    this.postCode = StringLegalizer.create(postCode).safeTrim().mandatory().end();
  }

  /**
   * @return An Optional of geographical data.
   */
  public Optional<Geo> getGeo() {
    return Optional.ofNullable(geo);
  }

  /**
   * @param geo Sets geographical data. Null is permitted if no such data are available.
   */
  public void setGeo(Geo geo) {
    this.geo = geo;
  }

  /**
   * @return Concatenated trimmed street + housenumber .
   */
  public String getStreetAndHouseNumber() {
    return StringLegalizer.create(getStreet() + " " + getHouseNumber()).safeTrim().end();
  }

  /**
   * Check if this Address is valid from a point of business rules.
   *
   * @return true if it is a valid address by business rules
   */
  public boolean isValid() {
    return street != null && !street.isEmpty()
        && city != null && !city.isEmpty()
        && postCode != null && !postCode.isEmpty();
  }

  @Override
  public String toString() {
    return new MyToStringBuilder(this)
        .append("name", name)
        .append("street", street)
        .append("houseNumber", houseNumber)
        .append("city", city)
        .append("postCode", postCode)
        .append("geo", geo)
        .toString();
  }
}
