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

package de.fornalik.tankschlau.geo;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * {@link Gson} converter which handles (de)serialization to an instance of {@link Address}
 * from JSON input of web API Tankerkoenig.de.
 */
public class AddressJsonAdapter extends TypeAdapter<Address> {

  @Override
  public Address read(final JsonReader in) throws JsonParseException {
    JsonObject jsonObject = JsonParser.parseReader(in).getAsJsonObject();

    /* 1a. Parse primitives with Gson's default adapter */
    Address address = new Gson().fromJson(jsonObject, Address.class);

    /* 1b. Gson sets String fields to empty if there are no JSON properties for it, ignoring
    any constructors or setters of Address, using reflection. As we have some mandatory
    fields here, we must manually post-validate the object. */
    if (!address.isValid()) throw new JsonParseException(
        "Some values for mandatory fields are missing. Object values:\n" + address);

    /* 2a. Handle custom types */
    Geo partialGeo = Geo.createFromJson(jsonObject);

    /* 2b. Gson provides double default 0.0 if it does not find expected properties within the JSON.
    Within an Address, our Geo instance is optional/nullable for the case that no geo data exist.
    Let's check for that and only set Address.geo when we got data for it. */
    if (partialGeo.getLatitude() != 0.0
        || partialGeo.getLongitude() != 0.0
        || partialGeo.getDistance().isPresent())
      address.setGeo(Geo.createFromJson(jsonObject));

    return address;
  }

  /**
   * Not implemented. Currently there's no need for it.
   */
  @Override
  public void write(
      JsonWriter jsonWriter, Address address) {
    throw new UnsupportedOperationException("Method not implemented.");
  }
}
