package de.fornalik.tankschlau.geo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.util.MyToStringBuilder;
import de.fornalik.tankschlau.util.StringLegalizer;

import java.util.Objects;
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
    this(street, city, postCode, null);
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

  /**
   * Creates a new {@link Address} by de-serializing a given {@link JsonObject}.
   *
   * @param in The {@link JsonObject} from which to convert to an {@link Address}
   * @return Instance of {@link Address}
   * @throws com.google.gson.JsonParseException if Address is invalid from a point of business
   *                                            rules as checked by {@link #isValid()}.
   */
  public static Address createFromJson(JsonObject in) {
    Objects.requireNonNull(in, "JsonObject must not be null.");

    Gson gson = new GsonBuilder()
        .registerTypeAdapter(Address.class, new AddressJsonAdapter())
        .create();

    return gson.fromJson(in, Address.class);
  }

  public String getName() {
    return name;
  }

  /**
   * @param name Sets name of a person or company. When passing null, it becomes an empty String.
   */
  public void setName(String name) {
    this.name = StringLegalizer.init(name).nullToEmpty().safeTrim().end();
  }

  public String getStreet() {
    return street;
  }

  /**
   * @param street Sets street (excluding house number).
   * @throws StringLegalizer.ValueException if we passed null or an empty String
   */
  public void setStreet(String street) {
    this.street = StringLegalizer.init(street).safeTrim().mandatory().end();
  }

  public String getHouseNumber() {
    return houseNumber;
  }

  /**
   * @param houseNumber Sets house number. When passing null, it becomes an empty String.
   */
  public void setHouseNumber(String houseNumber) {
    this.houseNumber = StringLegalizer.init(houseNumber).nullToEmpty().safeTrim().end();
  }

  public String getCity() {
    return city;
  }

  /**
   * @param city Sets city or place
   * @throws StringLegalizer.ValueException if we passed null or an empty String
   */
  public void setCity(String city) {
    this.city = StringLegalizer.init(city).safeTrim().mandatory().end();
  }

  public String getPostCode() {
    return postCode;
  }

  /**
   * @param postCode Sets post code
   * @throws StringLegalizer.ValueException if we passed null or an empty String
   */
  public void setPostCode(String postCode) {
    this.postCode = StringLegalizer.init(postCode).safeTrim().mandatory().end();
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
