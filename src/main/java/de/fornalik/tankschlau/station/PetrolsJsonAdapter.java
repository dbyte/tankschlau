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

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.util.HashSet;
import java.util.Set;

/**
 * A {@link Gson} JSON adapter which converts given JSON petrol data to a Set of {@link Petrol}.
 */
public class PetrolsJsonAdapter extends TypeAdapter<Set<Petrol>> {

  @Override
  public Set<Petrol> read(JsonReader in) {
    Gson gson = new Gson();

    PricesDto pricesDto = gson.fromJson(in, PricesDto.class);
    return pricesDto.toPetrols();
  }

  /**
   * Not implemented. Currently there's no need for it.
   */
  @Override
  public void write(JsonWriter out, Set<Petrol> values) {
    throw new UnsupportedOperationException("Method not implemented.");
  }

  /**
   * This is a concrete implementation for a JSON document having the structure (ex.) <br><br>
   * <code> {
   * [...]
   * "diesel": 1.099,
   * "e5": 1.585,
   * "e10": 1.6,
   * [...]
   * } </code><br><br>
   * As of our business rules, 0 to 3 of these elements may exist.
   */
  @SuppressWarnings("unused")
  private static class PricesDto {
    @SerializedName("diesel") private Double diesel;
    @SerializedName("e5") private Double e5;
    @SerializedName("e10") private Double e10;

    private Set<Petrol> toPetrols() {
      Set<Petrol> petrols = new HashSet<>(3);

      if (diesel != null && diesel != 0.0)
        petrols.add(new Petrol(PetrolType.DIESEL, diesel));

      if (e5 != null && e5 != 0.0)
        petrols.add(new Petrol(PetrolType.E5, e5));

      if (e10 != null && e10 != 0.0)
        petrols.add(new Petrol(PetrolType.E10, e10));

      return petrols;
    }
  }
}
