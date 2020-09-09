package de.fornalik.tankschlau.station;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.AddressBuilder;
import de.fornalik.tankschlau.geo.Coordinates2D;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

/**
 * Converter which handles (de)serialization for PetrolStation and
 * Tankerkoenig.de JSON response.
 */
public class PetrolStationsJsonAdapter extends TypeAdapter<List<PetrolStation>> {

  @Override
  public List<PetrolStation> read(JsonReader jsonReader) {
    ArrayList<PetrolStation> petrolStations = new ArrayList<>();

    JsonObject rootObj = JsonParser.parseReader(jsonReader).getAsJsonObject();
    JsonArray stations = rootObj.getAsJsonArray("stations");

    for (JsonElement jsonElement : stations) {
      if (!jsonElement.isJsonObject()) continue;
      JsonObject jsonStation = jsonElement.getAsJsonObject();

      petrolStations.add(createStation(jsonStation));
    }

    return petrolStations;
  }

  @Override
  public void write(JsonWriter jsonWriter, List<PetrolStation> petrolStation) {
    throw new UnsupportedOperationException("Method not implemented.");
  }

  private PetrolStation createStation(JsonObject station) {
    return PetrolStationBuilder.init()
        .setBrand(station.get("brand").getAsString())
        .setIsOpen(station.get("isOpen").getAsBoolean())
        .setDistanceKm(station.get("dist").getAsDouble())
        .setPetrols(adaptPetrols(station))
        .setAddress(adaptAddress(station))
        .build(adaptUUID(station));
  }

  private UUID adaptUUID(JsonObject station) {
    return UUID.fromString(station.get("id").getAsString());
  }

  private HashSet<Petrol> adaptPetrols(JsonObject station) {
    HashSet<Petrol> petrols = new HashSet<>();

    for (PetrolType petrolType : PetrolType.values()) {
      // Assuming that PetrolType.toLowerCase() matches the JSON keys!
      String jsonPetrolType = petrolType.name().toLowerCase();

      JsonElement price = station.get(jsonPetrolType);
      if (price != null && !price.isJsonNull() && price.getAsDouble() > 0.0)
        petrols.add(new Petrol(petrolType, price.getAsDouble()));
    }

    return petrols;
  }

  private Address adaptAddress(JsonObject station) {
    return AddressBuilder.init()
        .setMandatoryFields(
            station.get("street").getAsString(),
            station.get("place").getAsString(),
            station.get("postCode").getAsString())
        .setName(station.get("name").getAsString())
        .setHouseNumber(station.get("houseNumber").getAsString())
        .setCoordinates2D(adaptCoordinates2D(station))
        .build();
  }

  private Coordinates2D adaptCoordinates2D(JsonObject station) {
    return new Coordinates2D(
        station.get("lat").getAsDouble(),
        station.get("lng").getAsDouble()
    );
  }
}
