package de.fornalik.tankschlau.station;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.fornalik.tankschlau.geo.Distance;
import de.fornalik.tankschlau.helpers.response.FixtureFiles;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Assertions;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * PetrolStation factory for tests.
 * <p>
 * Use for creating fixed data of a {@link PetrolStation}.
 * All fields are public mutable for testing purposes. Also, all primitives are wrapped
 * to be able to null them for testing purposes.
 */
public class PetrolStationFixture {
  /*
  Caution: All field names must be exactly equal to Tankerkoenig.de JSON key "stations"
  in order to be able to convert the JSON fixture along with a Gson converter!
  */
  public Boolean ok;
  public String licence;
  public String data;
  public String status;
  public ArrayList<StationDTO> stations;

  private PetrolStationFixture() {
    stations = new ArrayList<>();
  }

  public static PetrolStationFixture newInstance() {
    return new PetrolStationFixture();
  }

  /**
   * Creates two test-fixture objects by reading a JSON response fixture file.<br/>
   * 1) a PetrolStationFixture which we can use e.g. for equality checks.<br/>
   * 2) a {@link Gson} JsonObject.
   *
   * @param resName Resource path as String. Note that the implicit resource root path must not
   *                 be included here.
   * @return Pair of PetrolStationFixture and JsonObject which is produced by reading a
   * JSON test-fixture resource file. Decompose by using .getLeft() and getRight(), see
   * {@link Pair#getLeft()} resp. {@link Pair#getRight()} <br/>
   * left: resulting fixture as instance of PetrolStationFixture <br/>
   * right: resulting fixture as instance of JsonObject
   */
  public static Pair<PetrolStationFixture, JsonObject> create_fromJsonFile(String resName) {
    Objects.requireNonNull(resName);

    FileReader reader1 = FixtureFiles.getFileReaderForResource(resName);
    FileReader reader2 = FixtureFiles.getFileReaderForResource(resName);
    Gson gson = new Gson();

    PetrolStationFixture objectFixture = gson.fromJson(reader1, PetrolStationFixture.class);
    JsonObject jsonFixture = (JsonObject) JsonParser.parseReader(reader2);

    return Pair.of(objectFixture, jsonFixture);
  }

  public static Pair<PetrolStationFixture, JsonObject> create_Berlin() {
    return create_fromJsonFile(
        FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);
  }

  /**
   * Deep check for value equality of a PetrolStationFixture with a PetrolStation.
   * @param petrolStation The {@link PetrolStation} to be checked for deep value equality.
   */
  public void assertEquals(PetrolStation petrolStation) {
    /* Preconditions for running the test. Note these checks are not subject to the test itself.
    Thus, we don't use Junit assertions here. */
    Objects.requireNonNull(petrolStation);
    Objects.requireNonNull(petrolStation.uuid);

    // Find required PetrolStationFixture for the PetrolStation under test.
    StationDTO fixture = stations.stream()
        .filter(fixt -> fixt.id.toString().equals(petrolStation.uuid.toString()))
        .findFirst()
        .orElse(null);

    Assertions.assertNotNull(fixture);

    Assertions.assertEquals(fixture.id, petrolStation.uuid);
    Assertions.assertEquals(fixture.brand, petrolStation.brand);
    Assertions.assertEquals(fixture.isOpen, petrolStation.isOpen);

    Assertions.assertEquals(Optional.of(fixture.dist),
                            petrolStation.getDistance().map(Distance::getKm));

    Assertions.assertNotNull(petrolStation.address);
    Assertions.assertEquals(fixture.name, petrolStation.address.getName());
    Assertions.assertEquals(fixture.street, petrolStation.address.getStreet());
    Assertions.assertEquals(fixture.houseNumber, petrolStation.address.getHouseNumber());
    Assertions.assertEquals(fixture.place, petrolStation.address.getCity());
    Assertions.assertEquals(fixture.postCode, petrolStation.address.getPostCode());

    Assertions.assertEquals(Optional.ofNullable(fixture.lat),
                            petrolStation.address.getCoordinates2D().map(c -> c.latitude));

    Assertions.assertEquals(Optional.ofNullable(fixture.lng),
                            petrolStation.address.getCoordinates2D().map(c -> c.longitude));

    Assertions.assertEquals(Optional.ofNullable(fixture.diesel),
                            petrolStation.getPetrolPrice(PetrolType.DIESEL));

    Assertions.assertEquals(Optional.ofNullable(fixture.e10),
                            petrolStation.getPetrolPrice(PetrolType.E10));

    Assertions.assertEquals(Optional.ofNullable(fixture.e5),
                            petrolStation.getPetrolPrice(PetrolType.E5));
  }

  /**
   * Transfer class to easily convert a Tankerkoenig.de API JSON response file to a
   * (petrol station) test-fixture class. We currently perform conversion with the {@link Gson}
   * library.
   */
  public static class StationDTO {
    /*
    Caution: All field names must be exactly equal to Tankerkoenig.de response JSON keys
    in order to be able to convert the JSON fixture along with a Gson converter!
    */
    public UUID id;
    public String name;
    public String brand;
    public boolean isOpen;
    public String street;
    public String houseNumber;
    public String place;
    public String postCode;
    public Double lat;
    public Double lng;
    public Double dist;
    public Double diesel;
    public Double e5;
    public Double e10;
  }
}
