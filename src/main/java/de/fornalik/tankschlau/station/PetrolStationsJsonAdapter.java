package de.fornalik.tankschlau.station;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.fornalik.tankschlau.geo.Coordinates2D;
import de.fornalik.tankschlau.geo.GeoLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PetrolStationsJsonAdapter extends TypeAdapter<List<PetrolStation>> {

  @Override
  public void write(
      JsonWriter jsonWriter, List<PetrolStation> petrolStation) {
    // Code here
  }

  @Override
  public List<PetrolStation> read(JsonReader jsonReader) {
    ArrayList<PetrolStation> petrolStation = new ArrayList<>();

    JsonObject rootObj = JsonParser.parseReader(jsonReader).getAsJsonObject();
    JsonArray stations = rootObj.getAsJsonArray("stations");

    for (JsonElement jsonElement : stations) {
      if (!jsonElement.isJsonObject()) continue;
      JsonObject jsonStation = jsonElement.getAsJsonObject();
      petrolStation.add(createStation(jsonStation));
    }

    return petrolStation;
  }

  private PetrolStation createStation(JsonObject station) {
    GeoLocation geoLocation = adaptGeoLocation(station);

    return new PetrolStationBuilder()
        .setBaseData(station.get("name").getAsString(),
                     station.get("brand").getAsString(),
                     station.get("place").getAsString())
        .setDistance(station.get("dist").getAsDouble())
        .setPetrols(adaptPetrols(station))
        .setGeoLocation(adaptGeoLocation(station))
        .create(adaptUUID(station));
  }

  private ArrayList<Petrol> adaptPetrols(JsonObject station) {
    ArrayList<Petrol> petrols = new ArrayList<>();
    JsonElement price;

    for (PetrolType petrolType : PetrolType.values()) {
      // Assuming that PetrolType.toLowerCase() matches the JSON keys!
      String jsonPetrolType = petrolType.name().toLowerCase();

      price = station.get(jsonPetrolType);
      if (price != null) petrols.add(new Petrol(petrolType, price.getAsDouble()));
    }

    return petrols;
  }

  private UUID adaptUUID(JsonObject station) {
    return UUID.fromString(station.get("id").getAsString());
  }

  private GeoLocation adaptGeoLocation(JsonObject station) {
    GeoLocation geoLocation = new GeoLocation();

    geoLocation.setStreet(station.get("street").getAsString());
    geoLocation.setHouseNumber(station.get("houseNumber").getAsString());
    geoLocation.setPostCode(station.get("postCode").getAsString());
    geoLocation.setCoordinates2D(adaptCoordinates2D(station));

    return geoLocation;
  }

  private Coordinates2D adaptCoordinates2D(JsonObject station) {
    return new Coordinates2D(
        station.get("lat").getAsDouble(),
        station.get("lng").getAsDouble()
    );
  }
}
