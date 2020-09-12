package de.fornalik.tankschlau.station;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.AddressBuilder;
import de.fornalik.tankschlau.geo.Coordinates2D;
import de.fornalik.tankschlau.geo.Distance;

import java.util.*;

/**
 * {@link Gson} converter which handles (de)serialization to a List of PetrolStations
 * from JSON input of web API Tankerkoenig.de.
 */
public class PetrolStationsJsonAdapter extends TypeAdapter<List<PetrolStation>> {
  private List<String> errorMessages = new ArrayList<>();

  /**
   * Use to check whether errors occurred during (de)serialization, as we treat that processes
   * as forgiving as possible, regarding data we got from a web API.
   * errorMessages are guaranteed to be reset each time {@link #read(JsonReader)}
   * or {@link #write(JsonWriter, List)} is called.
   *
   * @return A new copy of the collected errors which have occurred and suppressed during
   * deserialization or an empty List if no errors occurred.
   */
  public List<String> getErrorMessages() {
    return new ArrayList<>(errorMessages);
  }

  /**
   * @param jsonReader An instance of {@link JsonReader}
   * @return List of {@link PetrolStation} objects, or throws a specialized RTE
   * @throws PetrolStationsJsonAdapter.MissingElementException if expected JSON element was not
   *                                                           found in the JSON document
   * @implNote Permit lenient behavior and collect certain exceptions instead of throwing them.
   */
  @Override
  public List<PetrolStation> read(JsonReader jsonReader) {
    // https://creativecommons.tankerkoenig.de/json/list.php?lat=52.408306&lng=10.7720025&rad=10.0
    // &sort=dist&type=all&apikey=00000000-0000-0000-0000-000000000002
    Objects.requireNonNull(jsonReader);

    errorMessages = new ArrayList<>();
    ArrayList<PetrolStation> petrolStations = new ArrayList<>();
    JsonObject jsonRoot;

    /* Do not handle any occurring errors while converting to a JSON object, as there is a
    serious problem with the given JSON document IF they occur using parseReader(). */
    jsonRoot = JsonParser.parseReader(jsonReader).getAsJsonObject();

    JsonArray jsonStations = jsonRoot.getAsJsonArray("stations");
    if (jsonStations == null) {
      errorMessages.add("Required property \"stations\" missing in JSON.");
      return new ArrayList<>();
    }

    if (jsonStations.size() == 0) {
      errorMessages.add("Empty \"stations\" array in JSON.");
      return new ArrayList<>();
    }

    for (JsonElement jsonElement : jsonStations) {
      // Expect that each station array element is a JSON object
      if (!jsonElement.isJsonObject()) {
        errorMessages.add(
            "Expected a JSON object in \"stations\" array but got: " + jsonElement);
        continue;
      }

      JsonObject jsonStation = jsonElement.getAsJsonObject();

      try {
        petrolStations.add(createStation(jsonStation));
      } catch (MissingElementException | IllegalStateException e) {
        errorMessages.add(e.getMessage());
      }
    }

    return petrolStations;
  }

  private PetrolStation createStation(JsonObject station) {
    return PetrolStationBuilder.create(adaptUUID(station))
        .setBrand(station.get("brand").getAsString())
        .setIsOpen(station.get("isOpen").getAsBoolean())
        .setDistance(new Gson().fromJson(station, Distance.class))
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

  private Set<Petrol> adaptPetrols(JsonObject station) {
    HashSet<Petrol> petrols = new HashSet<>();

    for (PetrolType petrolType : PetrolType.values()) {
      // Assuming that PetrolType.toLowerCase() matches the JSON keys!
      String jsonPetrolType = petrolType.name().toLowerCase();

      JsonElement price = station.get(jsonPetrolType);
      if (price == null || price.isJsonNull() || price.getAsDouble() <= 0.0)  continue;

      petrols.add(new Petrol(petrolType, price.getAsDouble()));
    }

    return petrols;
  }

  private Address adaptAddress(JsonObject station) {
    // Caution, response: Possible _empty_ JsonStrings for: houseNumber

    new Gson().fromJson(station, Address.class);

    return AddressBuilder.init()
        .setMandatoryFields(
            station.get("street").getAsString(),
            station.get("place").getAsString(),
            station.get("postCode").getAsString())
        .setName(station.get("name").getAsString())
        .setHouseNumber(station.get("houseNumber").getAsString())
        .setCoordinates2D(new Gson().fromJson(station, Coordinates2D.class))
        .build();
  }

  /**
   * Not implemented. Currently there's no need for it.
   */
  @Override
  public void write(JsonWriter jsonWriter, List<PetrolStation> petrolStation) {
    throw new UnsupportedOperationException("Method not implemented.");
  }

  /**
   * Thrown if a mandatory JSON element is missing and app is not able to handle it straight away.
   *
   * @implNote This is an unchecked exception.
   */
  public static class MissingElementException extends RuntimeException {
    public MissingElementException(String property) {
      super("Required property \"" + property + "\" missing in JSON.");
    }
  }
}
