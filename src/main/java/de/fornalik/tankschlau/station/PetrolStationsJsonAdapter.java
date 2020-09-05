package de.fornalik.tankschlau.station;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.fornalik.tankschlau.geo.Coordinates2D;
import de.fornalik.tankschlau.geo.Distance;
import de.fornalik.tankschlau.geo.GeoLocation;

import java.io.IOException;
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
      JsonObject station = jsonElement.getAsJsonObject();
      petrolStation.add(createStation(station));
    }

    return petrolStation;
  }

  private PetrolStation createStation(JsonObject station) {
    UUID uuid = (UUID.fromString(station.get("id").getAsString()));
    String name = station.get("name").getAsString();
    String brand = station.get("brand").getAsString();
    String place = station.get("place").getAsString();
    boolean isOpen = station.get("isOpen").getAsBoolean();

    return new PetrolStation(
        uuid,
        name,
        brand,
        place,
        createGeoLocation(station),
        createDistance(station),
        createPetrols(station));
  }

  private ArrayList<Petrol> createPetrols(JsonObject station) {
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

  private GeoLocation createGeoLocation(JsonObject station) {
    GeoLocation geoLocation = new GeoLocation(
        station.get("street").getAsString(),
        station.get("houseNumber").getAsString(),
        station.get("postCode").getAsString());

    geoLocation.setCoordinates2D(createCoordinates2D(station));

    return geoLocation;
  }

  private Coordinates2D createCoordinates2D(JsonObject station) {
    return new Coordinates2D(
        station.get("lat").getAsDouble(),
        station.get("lng").getAsDouble()
    );
  }

  private Distance createDistance(JsonObject station) {
    return new Distance(station.get("dist").getAsDouble());
  }
}
