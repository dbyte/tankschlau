package de.fornalik.tankschlau.helpers.response.fixture;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.fornalik.tankschlau.geo.GeoLocation;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolType;

public class TankerkoenigResponseFixture {

  public JsonObject createJson(final PetrolStation petrolStation)
      throws PetrolStation.PriceException {

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

  private JsonObject createJsonPetrolStationWithAllFields(PetrolStation petrolStation)
      throws PetrolStation.PriceException {
    JsonObject station = new JsonObject();

    // Create all possible fields as of API documentation at Tankerkoenig.de.
    // So this marks a happy path regarding valid field names.
    station.addProperty("id", petrolStation.uuid.toString());
    station.addProperty("name", petrolStation.name);
    station.addProperty("brand", petrolStation.brand);
    station.addProperty("street", petrolStation.geoLocation.getStreet());
    station.addProperty("place", petrolStation.place);
    station.addProperty("lat", petrolStation.geoLocation.getCoordinates2D().latitude);
    station.addProperty("lng", petrolStation.geoLocation.getCoordinates2D().longitude);
    station.addProperty("dist", petrolStation.distance.getKm());
    station.addProperty("diesel", petrolStation.getPrice(PetrolType.DIESEL));
    station.addProperty("e5", petrolStation.getPrice(PetrolType.E5));
    station.addProperty("e10", petrolStation.getPrice(PetrolType.E10));
    station.addProperty("isOpen", true);
    station.addProperty("houseNumber", petrolStation.geoLocation.getHouseNumber());
    station.addProperty("postCode", petrolStation.geoLocation.getPostCode());

    return station;
  }
}
