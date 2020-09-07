package de.fornalik.tankschlau.station;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Coordinates2D;
import de.fornalik.tankschlau.util.StringLegalizer;

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
    ArrayList<PetrolStation> petrolStations = new ArrayList<>();

    JsonObject rootObj = JsonParser.parseReader(jsonReader).getAsJsonObject();
    JsonArray stations = rootObj.getAsJsonArray("stations");

    for (JsonElement jsonElement : stations) {
      if (!jsonElement.isJsonObject()) continue;
      JsonObject jsonStation = jsonElement.getAsJsonObject();

      try {
        petrolStations.add(createStation(jsonStation));
      } catch (StringLegalizer.ValueException e) {
        e.printStackTrace();
      }
    }

    return petrolStations;
  }

  private PetrolStation createStation(JsonObject station) throws StringLegalizer.ValueException {
    return new PetrolStationBuilder()
        .setBrand(station.get("brand").getAsString())
        .setDistance(station.get("dist").getAsDouble())
        .setPetrols(adaptPetrols(station))
        .setAddress(adaptAddress(station))
        .build(adaptUUID(station));
  }

  private ArrayList<Petrol> adaptPetrols(JsonObject station) {
    ArrayList<Petrol> petrols = new ArrayList<>();

    for (PetrolType petrolType : PetrolType.values()) {
      // Assuming that PetrolType.toLowerCase() matches the JSON keys!
      String jsonPetrolType = petrolType.name().toLowerCase();

      JsonElement price = station.get(jsonPetrolType);
      if (price != null)
        petrols.add(new Petrol(petrolType, price.getAsDouble()));
    }

    return petrols;
  }

  private UUID adaptUUID(JsonObject station) {
    return UUID.fromString(station.get("id").getAsString());
  }

  private Address adaptAddress(JsonObject station) throws StringLegalizer.ValueException {
    return new Address(
        station.get("name").getAsString(),
        station.get("street").getAsString(),
        station.get("houseNumber").getAsString(),
        station.get("place").getAsString(),
        station.get("postCode").getAsString(),
        adaptCoordinates2D(station));
  }

  private Coordinates2D adaptCoordinates2D(JsonObject station) {
    return new Coordinates2D(
        station.get("lat").getAsDouble(),
        station.get("lng").getAsDouble()
    );
  }
}
