/*
 * Copyright (c) 2020 Tammo Fornalik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fornalik.tankschlau.station;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.fornalik.tankschlau.geo.Address;

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
   * @param in An instance of {@link JsonReader}
   * @return List of {@link PetrolStation} objects, or throws a specialized RTE
   * @throws PetrolStationsJsonAdapter.MissingElementException if expected JSON element was not
   *                                                           found in the JSON document
   * @implNote Permit lenient behavior and collect certain exceptions instead of throwing them.
   */
  @Override
  public List<PetrolStation> read(JsonReader in) {
    // https://creativecommons.tankerkoenig.de/json/list.php?lat=52.408306&lng=10.7720025&rad=10.0
    // &sort=dist&type=all&apikey=00000000-0000-0000-0000-000000000002
    Objects.requireNonNull(in);

    errorMessages = new ArrayList<>();

    JsonObject jsonRoot = JsonParser.parseReader(in).getAsJsonObject();
    JsonArray jsonStations = jsonRoot.getAsJsonArray("stations");

    if (jsonStations == null) {
      errorMessages.add("Required property \"stations\" missing in JSON.");
      return new ArrayList<>();
    }

    if (jsonStations.size() == 0) {
      errorMessages.add("Empty \"stations\" array in JSON.");
      return new ArrayList<>();
    }

    return createStations(jsonStations);
  }

  private List<PetrolStation> createStations(JsonArray jsonStations) {
    ArrayList<PetrolStation> petrolStations = new ArrayList<>();

    for (JsonElement jsonElement : jsonStations) {
      // Expect that each station array element is a JSON object
      if (!jsonElement.isJsonObject()) {
        errorMessages.add(
            "Expected a JSON object in \"stations\" array but got: " + jsonElement);
        continue;
      }

      JsonObject jsonStation = jsonElement.getAsJsonObject();

      try {
        petrolStations.add(adaptStation(jsonStation));
      } catch (MissingElementException | JsonParseException e) {
        errorMessages.add(e.getMessage());
      }
    }

    return petrolStations;
  }

  private PetrolStation adaptStation(JsonObject station) {
    // 1. Parse Java lang objects/primitives with Gson's default adapter
    PetrolStation partialPetrolStation = new Gson().fromJson(station, PetrolStation.class);

    // 2. Build a new petrol station, by filling in previously created data and handling
    // custom types.
    return PetrolStationBuilder.create(adaptUUID(station))
                               .withBrand(partialPetrolStation.brand)
                               .withIsOpen(partialPetrolStation.isOpen)
                               .withPetrols(Petrols.createFromJson(station))
                               .withAddress(Address.createFromJson(station)) //throws
                               .build();
  }

  private UUID adaptUUID(JsonObject station) throws MissingElementException {
    String property = "id";

    return Optional.ofNullable(station.get(property))
                   .map(JsonElement::getAsString)
                   .map(UUID::fromString)
                   .orElseThrow(() -> new MissingElementException(property));
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
