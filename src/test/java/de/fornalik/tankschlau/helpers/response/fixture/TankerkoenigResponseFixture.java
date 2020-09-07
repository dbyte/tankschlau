package de.fornalik.tankschlau.helpers.response.fixture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.fornalik.tankschlau.geo.Coordinates2D;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolType;

public class TankerkoenigResponseFixture {

  public JsonObject createJson(final PetrolStation petrolStation) {

    JsonObject root = createJsonRoot_happy();
    JsonArray stations = new JsonArray();
    stations.add(createJsonPetrolStationWithAllFields(petrolStation));
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

  private JsonObject createJsonPetrolStationWithAllFields(PetrolStation petrolStation) {
    JsonObject station = new JsonObject();
    Coordinates2D zeroCoordinates = new Coordinates2D(0.0, 0.0);

    // Create all possible fields as of API documentation at Tankerkoenig.de.
    // So this marks a happy path regarding valid field names.
    station.addProperty("id", petrolStation.uuid.toString());
    station.addProperty("name", petrolStation.address.getName());
    station.addProperty("brand", petrolStation.brand);
    station.addProperty("street", petrolStation.address.getStreet());
    station.addProperty("place", petrolStation.address.getCity());
    station.addProperty("dist", petrolStation.distance.getKm());
    station.addProperty("diesel", petrolStation.getPrice(PetrolType.DIESEL).orElse(0.0));
    station.addProperty("e5", petrolStation.getPrice(PetrolType.E5).orElse(0.0));
    station.addProperty("e10", petrolStation.getPrice(PetrolType.E10).orElse(0.0));
    station.addProperty("isOpen", true);
    station.addProperty("houseNumber", petrolStation.address.getHouseNumber());
    station.addProperty("postCode", petrolStation.address.getPostCode());

    station.addProperty("lat", petrolStation.address
        .getCoordinates2D()
        .orElse(zeroCoordinates).latitude);

    station.addProperty("lng", petrolStation.address
        .getCoordinates2D()
        .orElse(zeroCoordinates).longitude);

    return station;
  }
}
