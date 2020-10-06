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

package de.fornalik.tankschlau.webserviceapi.tankerkoenig;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.station.Petrol;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolStationBuilder;
import de.fornalik.tankschlau.station.Petrols;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class TankerkoenigJsonAdapter {
  private final Gson jsonProvider;

  public TankerkoenigJsonAdapter(Gson jsonProvider) {
    this.jsonProvider = jsonProvider;
  }

  /**
   * Note: We do not catch exceptions here as major preconditions should have been
   * checked at call side. If something crashes here, there's a severe problem, so we really
   * do want a crash.
   *
   * @param jsonString The JSON string we got from tankerkoenig.de.
   * @return List of {@link PetrolStation} objects.
   * @throws JsonParseException    if the specified text is not valid JSON.
   * @throws NullPointerException  if some object could not be extracted.
   * @throws IllegalStateException if some object would be in a illegal state after creation.
   */
  List<PetrolStation> createPetrolStations(String jsonString) {
    if (jsonString == null || "".equals(jsonString)) {
      System.err.println("Log.Error: JSON string is null or empty: " + jsonString);
      return new ArrayList<>();
    }

    JsonElement stationsJsonElement = JsonParser
        .parseString(jsonString)
        .getAsJsonObject()
        .get("stations");

    if (stationsJsonElement == null || !stationsJsonElement.isJsonArray()) {
      System.err.println("Log.Error: No stations found in JSON: " + jsonString);
      return new ArrayList<>();
    }

    List<PetrolStation> petrolStations = new ArrayList<>();

    stationsJsonElement
        .getAsJsonArray()
        .iterator()
        .forEachRemaining(jsonElem -> {
          String str = jsonElem.getAsJsonObject().toString();
          PetrolStation pst = createSinglePetrolStation(str);
          petrolStations.add(pst);
        });

    return petrolStations;
  }

  private PetrolStation createSinglePetrolStation(String jsonStr) {
    /* 1. Parse primitives with Gson's default adapter */

    PetrolStation rawPetrolStation = jsonProvider.fromJson(jsonStr, PetrolStation.class);
    Set<Petrol> petrols = jsonProvider.fromJson(jsonStr, (Type) Petrols.class);
    Address rawAddress = jsonProvider.fromJson(jsonStr, Address.class);
    Geo geo = jsonProvider.fromJson(jsonStr, Geo.class);

    /* 2. Legalize to business contracts */

    // Legalize Address by passing it to its failable constructor.
    Address legalizedAddress = new Address(
        rawAddress.getName(),
        rawAddress.getStreet(),
        rawAddress.getHouseNumber(),
        rawAddress.getCity(),
        rawAddress.getPostCode(),
        null);

    // Validate Geo object and add it to address if valid.
    if (geo.getLatitude() != 0.0 || geo.getLongitude() != 0.0 || geo.getDistance().isPresent())
      legalizedAddress.setGeo(geo);

    /* 3. Create the final business object */
    return PetrolStationBuilder
        .create(rawPetrolStation.uuid)
        .withBrand(rawPetrolStation.brand)
        .withIsOpen(rawPetrolStation.isOpen)
        .withPetrols(petrols)
        .withAddress(legalizedAddress)
        .build();
  }
}
