package de.fornalik.tankschlau.helpers.response;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.Petrol;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStationBuilder;
import de.fornalik.tankschlau.station.PetrolType;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;

import java.io.FileReader;
import java.util.*;
import java.util.function.Function;

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
   * {@link Pair#getRight()}: a {@link JsonObject} of the <b>first {@link PetrolStation}</b>
   * found within the JSON file fixture.
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

  /**
   * Converts the generated stations List of type {@link StationDTO} to List<PetrolStation>
   *
   * @return A List of PetrolStation objects.
   */
  public List<PetrolStation> convertToPetrolStations() {
    List<PetrolStation> petrolStations = new ArrayList<>();

    for (StationDTO dto : stations) {
      Geo geo = new Geo(dto.lat, dto.lng, dto.distanceKm);

      Address address = new Address(
          dto.name,
          dto.street,
          dto.houseNumber,
          dto.city,
          dto.postCode,
          geo);

      Set<Petrol> petrols = new HashSet<>();
      petrols.add(new Petrol(PetrolType.DIESEL, dto.diesel));
      petrols.add(new Petrol(PetrolType.E10, dto.e10));
      petrols.add(new Petrol(PetrolType.E5, dto.e5));

      petrolStations.add(
          PetrolStationBuilder
              .create(dto.uuid)
              .withBrand(dto.brand)
              .withIsOpen(dto.isOpen)
              .withAddress(address)
              .withPetrols(petrols)
              .build());
    }

    return petrolStations;
  }

  // region assertEqual helpers

  public void assertEqualsIgnoringSort(List<PetrolStation> petrolStations) {
    Objects.requireNonNull(petrolStations);
    Assertions.assertEquals(this.stations.size(), petrolStations.size());
    petrolStations.forEach(this::assertEqualsIgnoringSort);
  }

  /**
   * Deep check for value equality of a JsonResponseFixture with a {@link PetrolStation}.
   *
   * @param petrolStation The {@link PetrolStation} to be checked for deep value equality.
   */
  public void assertEqualsIgnoringSort(PetrolStation petrolStation) {
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
    this.assertEqualsIgnoringSort(petrolStation.address, stations.indexOf(fixture));

    Set<Petrol> actualPetrols = new HashSet<>(petrolStation.getPetrols());
    this.assertEqualsIgnoringSort(actualPetrols, stations.indexOf(fixture));
  }

  /**
   * Helper for equality tests. Testing JSON response fixture against a resulting
   * {@link Address} instance.
   *
   * @param addressUnderTest The Address object to test for equality with the generated fixture.
   * @param fixtureIdx       Array index of the generated PetrolStation fixture to compare with.
   */
  public void assertEqualsIgnoringSort(Address addressUnderTest, int fixtureIdx) {
    /* Preconditions for running the test. Note these checks are not subject to the test itself.
    Thus, we don't use Junit assertions here. */

    assert addressUnderTest != null;

    // Get station by given index
    StationDTO fixture = stations.get(fixtureIdx);
    assert fixture != null;

    // Begin test

    Assertions.assertEquals(fixture.name, addressUnderTest.getName());
    Assertions.assertEquals(fixture.street, addressUnderTest.getStreet());
    Assertions.assertEquals(fixture.houseNumber, addressUnderTest.getHouseNumber());
    Assertions.assertEquals(fixture.city, addressUnderTest.getCity());
    Assertions.assertEquals(fixture.postCode, addressUnderTest.getPostCode());

    this.assertEqualsIgnoringSort(addressUnderTest.getGeo().orElse(null), fixtureIdx);
  }

  /**
   * Helper for equality tests. Testing JSON response fixture against a resulting
   * {@link Geo} instance.
   *
   * @param geoUnderTest The Geo object to test for equality with the generated fixture. Null is
   *                     explicitly <b>allowed</b>, respecting equality checks of Optional.empty().
   * @param fixtureIdx   Array index of the generated PetrolStation fixture to compare with.
   */
  public void assertEqualsIgnoringSort(Geo geoUnderTest, int fixtureIdx) {
    Optional<Geo> optGeoUnderTest = Optional.ofNullable(geoUnderTest);

    // Get station by given index
    StationDTO fixture = stations.get(fixtureIdx);
    assert fixture != null;

    // Begin test

    Assertions.assertEquals(
        Optional.of(fixture.lat),
        optGeoUnderTest.map(g -> g.latitude));

    Assertions.assertEquals(
        Optional.of(fixture.lng),
        optGeoUnderTest.map(g -> g.longitude));

    Assertions.assertEquals(
        Optional.ofNullable(fixture.distanceKm),
        optGeoUnderTest.flatMap(Geo::getDistance));
  }

  /**
   * @param petrolSet  The {@link Petrol} objects boxed into a Set to test for equality with the
   *                   generated Petrols fixture.
   * @param fixtureIdx Array index of the generated PetrolStation fixture to compare with.
   */
  public void assertEqualsIgnoringSort(Set<Petrol> petrolSet, int fixtureIdx) {
    assert petrolSet != null;

    // Get station by given index
    StationDTO fixture = stations.get(fixtureIdx);
    assert fixture != null;

    // Find price of object under test by given PetrolType
    Function<PetrolType, Optional<Double>> actualPrice = (ofType) -> (Optional<Double>) petrolSet
        .stream()
        .filter(petr -> petr.type == ofType)
        .findFirst()
        .map(petr -> petr.price);

    Assertions.assertEquals(Optional.ofNullable(fixture.diesel),
                            actualPrice.apply(PetrolType.DIESEL));

    Assertions.assertEquals(Optional.ofNullable(fixture.e10),
                            actualPrice.apply(PetrolType.E10));

    Assertions.assertEquals(Optional.ofNullable(fixture.e5),
                            actualPrice.apply(PetrolType.E5));
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
