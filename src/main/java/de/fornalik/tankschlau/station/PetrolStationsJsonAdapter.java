package de.fornalik.tankschlau.station;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.AddressBuilder;
import de.fornalik.tankschlau.geo.Coordinates2D;

import java.util.*;

/**
 * Converter which handles (de)serialization for PetrolStation and
 * Tankerkoenig.de JSON response.
 */
public class PetrolStationsJsonAdapter extends TypeAdapter<List<PetrolStation>> {

  @Override
  public List<PetrolStation> read(JsonReader jsonReader) {
    // https://creativecommons.tankerkoenig.de/json/list.php?lat=52.408306&lng=10.7720025&rad=5.0
    // &sort=dist&type=all&apikey=00000000-0000-0000-0000-000000000002

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
    // Caution, response: Possible _empty_ JsonStrings for: brand

    return PetrolStationBuilder.create(adaptUUID(station))
        .setBrand(station.get("brand").getAsString())
        .setIsOpen(station.get("isOpen").getAsBoolean())
        .setDistanceKm(station.get("dist").getAsDouble())
        .setPetrols(adaptPetrols(station))
        .setAddress(adaptAddress(station))
        .build();
  }

  private UUID adaptUUID(JsonObject station) throws MissingElementException {
    String property = "id";

    return Optional.ofNullable(station.get(property))
        .map(JsonElement::getAsString)
        .map(UUID::fromString)
        .orElseThrow(() -> new MissingElementException(property));
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
    // Caution, response: Possible _empty_ JsonStrings for: houseNumber
    // Caution, response: Incoming postCode is of type integer

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

 /* private <T> T forceDefaultValueIfNull(JsonElement elem, Class<T> clazz) {
    T out = null;

    if (clazz == String.class)
      out = elem != null ? (T) elem.getAsString() : (T) "";

    else if (clazz == Double.class)
      out = elem != null ? (T) Double.valueOf(elem.getAsDouble()) : (T) Double.valueOf(0.0);

    else if (clazz == Boolean.class)
      out = elem != null ? (T) Boolean.valueOf(elem.getAsBoolean()) : (T) Boolean.valueOf("false");

    if (null != out) return out;

    throw new UnsupportedOperationException("Type not available for default value.");
  }*/

  /**
   * Thrown if a mandatory JSON element is missing and app is not able to handle it straight away.
   *
   * @implNote This is an unchecked exception.
   */
  public static class MissingElementException extends RuntimeException {
    public MissingElementException(String property) {
      super("Required property " + property + " missing in received JSON document.");
    }
  }
}
