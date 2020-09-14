package de.fornalik.tankschlau.helpers.response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolType;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;

import java.io.FileReader;
import java.util.*;

/**
 * PetrolStation factory for tests.
 * <p>
 * Use for creating fixed data of a {@link PetrolStation}.
 * All fields are public mutable for testing purposes. Also, all primitives are wrapped
 * to be able to null them for testing purposes.
 */
public class JsonResponseFixture {

  @SerializedName("ok") public Boolean ok;
  @SerializedName("license") public String license;
  @SerializedName("data") public String data;
  @SerializedName("status") public String status;
  @SerializedName("stations") public ArrayList<StationDTO> stations;

  private JsonResponseFixture() {
    stations = new ArrayList<>();
  }

  public static JsonResponseFixture create() {
    return new JsonResponseFixture();
  }

  /**
   * Creates two test-fixture objects by reading a JSON response fixture file.<br/>
   * 1) a JsonResponseFixture which we can use e.g. for equality checks.<br/>
   * 2) a {@link JsonObject} of the JSON file fixture.
   *
   * @param resName Resource path as String. Note that the implicit resource root path must not
   *                be included here.
   * @return Pair of JsonResponseFixture and JsonObject which is produced by reading a
   * JSON test-fixture resource file. Decompose by using .getLeft() and getRight(), see
   * {@link Pair#getLeft()} resp. {@link Pair#getRight()} <br/>
   * left: resulting fixture as instance of JsonResponseFixture <br/>
   * right: resulting fixture as instance of JsonObject
   */
  public static Pair<JsonResponseFixture, JsonObject> createFromJsonFile(String resName) {
    Objects.requireNonNull(resName);

    FileReader reader1 = FixtureFiles.getFileReaderForResource(resName);
    FileReader reader2 = FixtureFiles.getFileReaderForResource(resName);
    Gson gson = new Gson();

    JsonResponseFixture objectFixture = gson.fromJson(reader1, JsonResponseFixture.class);
    JsonObject jsonFixture = (JsonObject) JsonParser.parseReader(reader2);

    return Pair.of(objectFixture, jsonFixture);
  }

  /**
   * Creates two test-fixture objects by reading a JSON response fixture file.<br/>
   * The returned JSON of this method does only include the <b>first station</b> of the response!
   * <p>
   * 1) a JsonResponseFixture which we can use e.g. for equality checks.<br/>
   * 2) a {@link JsonObject} of the <b>first station</b> found within the JSON file fixture.
   *
   * @return {@link Pair#getLeft()}: JsonResponseFixture which we can use e.g. for equality
   * checks.<br/>
   * {@link Pair#getRight()}: a {@link JsonObject} of the <b>first station</b> found within the
   * JSON file fixture.
   * @see #createFromJsonFile(String resName)
   */
  public static Pair<JsonResponseFixture, JsonObject> createFirstStationFromJsonFile(String resName) {
    Objects.requireNonNull(resName);

    Pair<JsonResponseFixture, JsonObject> responseFixture = createFromJsonFile(resName);

    assert responseFixture.getRight().getAsJsonArray("stations") != null;

    JsonObject jsonFirstStationOfStationArrayFixture = responseFixture.getRight()
        .getAsJsonArray("stations")
        .get(0)
        .getAsJsonObject();

    return Pair.of(responseFixture.getLeft(), jsonFirstStationOfStationArrayFixture);
  }

  // region assertEqual helpers

  public void assertEquals(List<PetrolStation> petrolStations) {
    Objects.requireNonNull(petrolStations);
    petrolStations.forEach(this::assertEquals);
  }

  /**
   * Deep check for value equality of a JsonResponseFixture with a {@link PetrolStation}.
   *
   * @param petrolStation The {@link PetrolStation} to be checked for deep value equality.
   */
  public void assertEquals(PetrolStation petrolStation) {
    /* Preconditions for running the test. Note these checks are not subject to the test itself.
    Thus, we don't use Junit assertions here. */

    assert petrolStation != null;

    // Find required JsonResponseFixture for the PetrolStation under test.
    StationDTO fixture = stations.stream()
        .filter(fixt -> fixt.uuid.equals(petrolStation.uuid))
        .findFirst()
        .orElse(null);

    assert fixture != null;

    // Begin test

    Assertions.assertEquals(fixture.uuid, petrolStation.uuid);
    Assertions.assertEquals(fixture.brand, petrolStation.brand);
    Assertions.assertEquals(fixture.isOpen, petrolStation.isOpen);

    Assertions.assertNotNull(petrolStation.address);
    this.assertEquals(petrolStation.address);

    Assertions.assertEquals(Optional.ofNullable(fixture.diesel),
                            petrolStation.getPetrolPrice(PetrolType.DIESEL));

    Assertions.assertEquals(Optional.ofNullable(fixture.e10),
                            petrolStation.getPetrolPrice(PetrolType.E10));

    Assertions.assertEquals(Optional.ofNullable(fixture.e5),
                            petrolStation.getPetrolPrice(PetrolType.E5));
  }

  /**
   * Helper for equality tests. Testing JSON response fixture against a resulting
   * {@link Address} instance.
   *
   * @param addressUnderTest The Address object to test for equality with the generated fixture.
   */
  public void assertEquals(Address addressUnderTest) {
    /* Preconditions for running the test. Note these checks are not subject to the test itself.
    Thus, we don't use Junit assertions here. */

    assert addressUnderTest != null;

    // Get first station (which itself contains properties lat, lng, dist) of JsonResponseFixture
    // for the Geo object under test.
    StationDTO fixture = stations.stream()
        .findFirst()
        .orElse(null);

    assert fixture != null;

    // Begin test

    Assertions.assertEquals(fixture.name, addressUnderTest.getName());
    Assertions.assertEquals(fixture.street, addressUnderTest.getStreet());
    Assertions.assertEquals(fixture.houseNumber, addressUnderTest.getHouseNumber());
    Assertions.assertEquals(fixture.city, addressUnderTest.getCity());
    Assertions.assertEquals(fixture.postCode, addressUnderTest.getPostCode());

    this.assertEquals(addressUnderTest.getGeo().orElse(null));
  }

  /**
   * Helper for equality tests. Testing JSON response fixture against a resulting
   * {@link Geo} instance.
   *
   * @param geoUnderTest The Geo object to test for equality with the generated fixture. Null is
   *                     explicitly <b>allowed</b>, respecting equality checks of Optional.empty().
   */
  public void assertEquals(Geo geoUnderTest) {
    Optional<Geo> optGeoUnderTest = Optional.ofNullable(geoUnderTest);

    // Get first station (which itself contains properties lat, lng, dist) of JsonResponseFixture
    // for the Geo object under test.
    StationDTO fixture = stations.stream()
        .findFirst()
        .orElse(null);

    assert fixture != null;

    // Begin test

    Assertions.assertEquals(Optional.of(fixture.lat),
                            optGeoUnderTest.map(g -> g.latitude));

    Assertions.assertEquals(Optional.of(fixture.lng),
                            optGeoUnderTest.map(g -> g.longitude));

    Assertions.assertEquals(Optional.ofNullable(fixture.distanceKm),
                            optGeoUnderTest.flatMap(Geo::getDistance));
  }

  // endregion

  /**
   * Transfer class to easily convert a Tankerkoenig.de API JSON response file to a
   * (petrol station) test-fixture instance. Conversion is currently processed by the {@link Gson}
   * library.
   */
  public static class StationDTO {
    @SerializedName("id") public UUID uuid;
    @SerializedName("name") public String name;
    @SerializedName("brand") public String brand;
    @SerializedName("isOpen") public boolean isOpen;
    @SerializedName("street") public String street;
    @SerializedName("houseNumber") public String houseNumber;
    @SerializedName("place") public String city;
    @SerializedName("postCode") public String postCode;
    @SerializedName("lat") public Double lat;
    @SerializedName("lng") public Double lng;
    @SerializedName("dist") public Double distanceKm;
    @SerializedName("diesel") public Double diesel;
    @SerializedName("e5") public Double e5;
    @SerializedName("e10") public Double e10;
  }
}
