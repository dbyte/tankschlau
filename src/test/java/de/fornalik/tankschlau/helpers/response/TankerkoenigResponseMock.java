package de.fornalik.tankschlau.helpers.response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.fornalik.tankschlau.station.PetrolStationFixture;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class TankerkoenigResponseMock {

  private final PetrolStationFixture fixture;
  private final List<String> excludedJsonProperties;

  private TankerkoenigResponseMock(
      PetrolStationFixture fixture,
      String... excludedJsonProperties) {

    this.fixture = Objects.requireNonNull(fixture);
    this.excludedJsonProperties = excludedJsonProperties != null
        ? Arrays.asList(excludedJsonProperties) : Collections.emptyList();
  }

  public static TankerkoenigResponseMock newInstance(PetrolStationFixture fixture) {
    return new TankerkoenigResponseMock(fixture, "");
  }

  public static TankerkoenigResponseMock newInstance(
      PetrolStationFixture fixture,
      String... excludedJsonProperties) {

    Objects.requireNonNull(fixture);
    return new TankerkoenigResponseMock(fixture, excludedJsonProperties);
  }

  public JsonObject createJsonResponse() {
    JsonObject root = createJsonRoot_happy();

    JsonArray stations = new JsonArray();
    stations.add(createJsonPetrolStation());

    root.add("stations", stations);
    return root;
  }

  private JsonObject createJsonRoot_happy() {
    JsonObject root = new JsonObject();

    root.addProperty("ok", true);
    root.addProperty("license", "CC BY 4.0 -  https://creativecommons.tankerkoenig.de");
    root.addProperty("data", "MTS-K");
    root.addProperty("status", "ok");

    return root;
  }

  /**
   * Create fields as of API documentation at Tankerkoenig.de, while <b>excluding</> those which
   * have been defined at the time this instance was created.
   *
   * @return Gson JSON object as defined by fixture input and excluded keys
   */
  private JsonObject createJsonPetrolStation() {

    JsonObject station = new JsonObject();

    station.addProperty("id", fixture.uuid.toString());
    station.addProperty("name", fixture.name);
    station.addProperty("brand", fixture.brand);
    station.addProperty("isOpen", fixture.isOpen);

    station.addProperty("street", fixture.street);
    station.addProperty("houseNumber", fixture.houseNumber);
    station.addProperty("postCode", fixture.postCode);
    station.addProperty("lat", fixture.lat);
    station.addProperty("lng", fixture.lng);
    station.addProperty("place", fixture.city);
    station.addProperty("dist", fixture.distanceKm);

    station.addProperty("diesel", fixture.priceDiesel);
    station.addProperty("e5", fixture.priceE5);
    station.addProperty("e10", fixture.priceE10);

    if (excludedJsonProperties != null && excludedJsonProperties.size() > 0)
      excludedJsonProperties.forEach(station::remove);

    return station;
  }
}
