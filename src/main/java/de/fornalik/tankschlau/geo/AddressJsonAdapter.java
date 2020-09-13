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

    // 1. Parse primitives with Gson's default adapter
    Address address = new Gson().fromJson(jsonObject, Address.class);

    // 2. Handle custom types
    address.setGeo(new Gson().fromJson(jsonObject, Geo.class));

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
