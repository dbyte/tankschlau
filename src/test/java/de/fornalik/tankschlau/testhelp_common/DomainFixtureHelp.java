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

package de.fornalik.tankschlau.testhelp_common;

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
import de.fornalik.tankschlau.storage.PetrolStationsService;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigResponse;
import org.junit.jupiter.api.Assertions;

import java.io.FileReader;
import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Deals with test-fixtures for the common domain of this app.
 * <p>
 * Use for creating fixed data of {@link PetrolStation} and
 * {@link PetrolStationsService}.
 * All DTO fields are public mutable for testing purposes. Also, all primitives are wrapped
 * to be able to null them for testing purposes.
 */
public class DomainFixtureHelp {
  public ResponseDTO objectFixture;
  public String jsonFixture;

  public DomainFixtureHelp() {
    this.objectFixture = new ResponseDTO();
  }

  /**
   * Computes two test-fixture objects by reading a JSON response fixture file.<br/>
   * 1) {@link ResponseDTO} which we can use e.g. for equality checks.<br/>
   * 2) {@link JsonObject} of the JSON file fixture.
   *
   * @param resName Resource path as String. Note that the implicit resource root path must not
   *                be included here.
   */
  public void setupFixture(String resName) {
    Objects.requireNonNull(resName);

    FileReader reader1 = ClassLoaderHelp.getFileReaderForResource(resName, getClass());
    FileReader reader2 = ClassLoaderHelp.getFileReaderForResource(resName, getClass());

    Gson gson = new Gson();

    objectFixture = gson.fromJson(reader1, ResponseDTO.class);
    jsonFixture = JsonParser.parseReader(reader2).getAsJsonObject().toString();
  }

  /**
   * Computes two test-fixture objects by reading a JSON response fixture file.<br/>
   * The returned JSON of this method does only include the <b>first station</b> of the response!
   * <p>
   * 1) {@link ResponseDTO} which we can use e.g. for equality checks.<br/>
   * 2) {@link JsonObject} of the <b>first station</b> found within the JSON file fixture.
   *
   * @see #setupFixture(String resName)
   */
  public void setupSingleFixture(String resName) {
    Objects.requireNonNull(resName);

    setupFixture(resName);

    JsonObject jsonObject = JsonParser.parseString(jsonFixture).getAsJsonObject();
    assert jsonObject.getAsJsonArray("stations") != null;

    jsonFixture = jsonObject
        .getAsJsonArray("stations")
        .get(0)
        .getAsJsonObject()
        .toString();
  }

  /**
   * Converts the generated stations List of type {@link StationDTO} to List<PetrolStation>
   *
   * @return A List of PetrolStation objects.
   */
  public List<PetrolStation> convertToPetrolStations() {
    List<PetrolStation> petrolStations = new ArrayList<>();

    for (StationDTO dto : objectFixture.stations) {
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

  // region Assertions

  public void assertEqualValues(TankerkoenigResponse tankerkoenigResponse) {
    Objects.requireNonNull(tankerkoenigResponse);

    assertEquals(objectFixture.status, tankerkoenigResponse.getStatus());
    assertEquals(objectFixture.getLicenseString(), tankerkoenigResponse.getLicenseString());
    assertEquals(objectFixture.message, tankerkoenigResponse.getErrorMessage().orElse(""));
    assertEquals(objectFixture.status, tankerkoenigResponse.getStatus());
  }

  /**
   * List version of {@link #assertEqualValues(PetrolStation)}.
   *
   * @param petrolStations The {@link PetrolStation} list to be checked for deep value equality.
   */
  public void assertEqualValuesIgnoringSort(List<PetrolStation> petrolStations) {
    Objects.requireNonNull(petrolStations);
    assertEquals(objectFixture.stations.size(), petrolStations.size());
    petrolStations.forEach(this::assertEqualValues);
  }

  /**
   * Deep check for value equality of a fixture with a {@link PetrolStation}.
   *
   * @param petrolStation The {@link PetrolStation} to be checked for deep value equality.
   */
  public void assertEqualValues(PetrolStation petrolStation) {
    /* Preconditions for running the test. Note these checks are not subject to the test itself.
    Thus, we don't use Junit assertions here. */

    assert petrolStation != null;

    // Find required fixture for the PetrolStation under test.
    StationDTO fixture = objectFixture.stations
        .stream()
        .filter(fixt -> fixt.uuid.equals(petrolStation.uuid))
        .findFirst()
        .orElse(null);

    assert fixture != null;

    // Begin test

    assertEquals(fixture.uuid, petrolStation.uuid);
    assertEquals(fixture.brand, petrolStation.brand);
    assertEquals(fixture.isOpen, petrolStation.isOpen);

    Assertions.assertNotNull(petrolStation.address);
    this.assertEqualValues(petrolStation.address, objectFixture.stations.indexOf(fixture));

    Set<Petrol> actualPetrols = new HashSet<>(petrolStation.getPetrols());
    this.assertEqualValuesIgnoringSort(actualPetrols, objectFixture.stations.indexOf(fixture));
  }

  /**
   * Helper for equality tests. Testing JSON response fixture against a resulting
   * {@link Address} instance.
   *
   * @param addressUnderTest The Address object to test for equality with the generated fixture.
   * @param fixtureIdx       Array index of the generated PetrolStation fixture to compare with.
   */
  public void assertEqualValues(Address addressUnderTest, int fixtureIdx) {
    /* Preconditions for running the test. Note these checks are not subject to the test itself.
    Thus, we don't use Junit assertions here. */
    assert addressUnderTest != null;

    // Get station by given index
    StationDTO fixture = objectFixture.stations.get(fixtureIdx);
    assert fixture != null;

    // Begin test

    assertEquals(fixture.name, addressUnderTest.getName());
    assertEquals(fixture.street, addressUnderTest.getStreet());
    assertEquals(fixture.houseNumber, addressUnderTest.getHouseNumber());
    assertEquals(fixture.city, addressUnderTest.getCity());
    assertEquals(fixture.postCode, addressUnderTest.getPostCode());

    this.assertEqualValues(addressUnderTest.getGeo().orElse(null), fixtureIdx);
  }

  /**
   * Helper for equality tests. Testing JSON response fixture against a resulting
   * {@link Geo} instance.
   *
   * @param geoUnderTest The Geo object to test for equality with the generated fixture. Null is
   *                     explicitly <b>allowed</b>, respecting equality checks of Optional.empty().
   * @param fixtureIdx   Array index of the generated PetrolStation fixture to compare with.
   */
  public void assertEqualValues(Geo geoUnderTest, int fixtureIdx) {
    Optional<Geo> optGeoUnderTest = Optional.ofNullable(geoUnderTest);

    // Get station by given index
    StationDTO fixture = objectFixture.stations.get(fixtureIdx);
    assert fixture != null;

    // Begin test

    assertEquals(
        Optional.of(fixture.lat),
        optGeoUnderTest.map(Geo::getLatitude));

    assertEquals(
        Optional.of(fixture.lng),
        optGeoUnderTest.map(Geo::getLongitude));

    assertEquals(
        Optional.ofNullable(fixture.distanceKm),
        optGeoUnderTest.flatMap(Geo::getDistance));
  }

  /**
   * @param petrolSet  The {@link Petrol} objects boxed into a Set to test for equality with the
   *                   generated Petrols fixture.
   * @param fixtureIdx Array index of the generated PetrolStation fixture to compare with.
   */
  public void assertEqualValuesIgnoringSort(Set<Petrol> petrolSet, int fixtureIdx) {
    assert petrolSet != null;

    // Get station by given index
    StationDTO fixture = objectFixture.stations.get(fixtureIdx);
    assert fixture != null;

    // Find price of object under test by given PetrolType
    Function<PetrolType, Optional<Double>> actualPrice = (ofType) -> (Optional<Double>) petrolSet
        .stream()
        .filter(petr -> petr.type == ofType)
        .findFirst()
        .map(petr -> petr.price);

    assertEquals(
        Optional.ofNullable(fixture.diesel),
        actualPrice.apply(PetrolType.DIESEL));

    assertEquals(
        Optional.ofNullable(fixture.e10),
        actualPrice.apply(PetrolType.E10));

    assertEquals(
        Optional.ofNullable(fixture.e5),
        actualPrice.apply(PetrolType.E5));
  }

  // endregion

  /**
   * Transfer class to easily convert a Tankerkoenig.de API JSON response file to a
   * test-fixture response. Conversion is currently processed by the {@link Gson}
   * library. <br>
   * All DTO fields are public mutable for testing purposes. Also, all primitives are wrapped
   * to be able to null them for testing purposes.
   */
  public static class ResponseDTO {
    @SerializedName("ok") public Boolean ok;
    @SerializedName("data") public String data;
    @SerializedName("status") public String status;
    @SerializedName("message") public String message;
    @SerializedName("stations") public ArrayList<StationDTO> stations;
    @SerializedName("license") private String license;

    ResponseDTO() {
      stations = new ArrayList<>();

      // As "message" is missing in JSON if no error occurred, it is by convention set
      // to empty in our production service class. So we do same here.
      message = "";
    }

    public String getLicenseString() {
      return (license != null ? license : "");
    }
  }

  /**
   * Transfer class to easily convert a Tankerkoenig.de API JSON response file to a
   * (petrol station) test-fixture. Conversion is currently processed by the {@link Gson}
   * library. <br>
   * All DTO fields are public mutable for testing purposes. Also, all primitives are wrapped
   * to be able to null them for testing purposes.
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
