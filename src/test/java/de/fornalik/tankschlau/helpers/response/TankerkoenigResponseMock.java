package de.fornalik.tankschlau.helpers.response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolType;

public class TankerkoenigResponseMock {

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
    /*  We really need the Optionals to contain values here because the incoming PetrolStation object
     might be value-compared to a resulting PetrolStation by some tests. Using an alternate
     value if an Optional is empty would cause these checks for equality to fail. */
    assert petrolStation.getPetrolPrice(PetrolType.DIESEL).isPresent();
    assert petrolStation.getPetrolPrice(PetrolType.E10).isPresent();
    assert petrolStation.getPetrolPrice(PetrolType.E5).isPresent();
    assert petrolStation.getDistance().isPresent();
    assert petrolStation.address.getCoordinates2D().isPresent();

    JsonObject station = new JsonObject();

   /*  Create all possible fields as of API documentation at Tankerkoenig.de.
     So this marks a happy path regarding valid field names. */
    station.addProperty("id", petrolStation.uuid.toString());
    station.addProperty("name", petrolStation.address.getName());
    station.addProperty("brand", petrolStation.brand);
    station.addProperty("street", petrolStation.address.getStreet());
    station.addProperty("place", petrolStation.address.getCity());
    station.addProperty("dist", petrolStation.getDistance().get().getKm());
    station.addProperty("diesel", petrolStation.getPetrolPrice(PetrolType.DIESEL).get());
    station.addProperty("e5", petrolStation.getPetrolPrice(PetrolType.E5).get());
    station.addProperty("e10", petrolStation.getPetrolPrice(PetrolType.E10).get());
    station.addProperty("isOpen", petrolStation.isOpen);
    station.addProperty("houseNumber", petrolStation.address.getHouseNumber());
    station.addProperty("postCode", petrolStation.address.getPostCode());
    station.addProperty("lat", petrolStation.address.getCoordinates2D().get().latitude);
    station.addProperty("lng", petrolStation.address.getCoordinates2D().get().longitude);

    return station;
  }
}
