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

import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.util.StringLegalizer;

import java.io.IOException;
import java.util.List;

/**
 * Interface for each Data Access Object of {@link PetrolStation}.
 */
public interface PetrolStationsDao {

  /**
   * Gets a list of petrol stations around the user's neighbourhood, whereby neighbourhood
   * is defined by the given {@link Geo} data of the user.
   *
   * @param geo {@link Geo} instance. Its latitude & longitude should reflect the current
   *            location of the user. Its distance field should reflect the maximum search radius
   *            for petrol stations in the neighbourhood of the users current location.
   * @return A list of {@link PetrolStation}, or an empty list if no stations were found.
   * @throws IOException If something went wrong while contacting the backing store of
   *                     {@link PetrolStation}.
   */
  List<PetrolStation> getAllInNeighbourhood(Geo geo) throws IOException;

  /**
   * @return Some technical response data - sort of response summary or header data,
   * depending on the concrete implementation.
   */
  TransactionInfo getTransactionInfo();

  /**
   * Provides some technical response data - sort of response summary or header data,
   * depending on the concrete implementation.
   */
  @SuppressWarnings("unused")
  class TransactionInfo {
    @SerializedName("ok") private boolean ok;
    @SerializedName("license") private String license;
    @SerializedName("status") private String status;
    @SerializedName("message") private String message;

    public TransactionInfo() {}

    public boolean isOk() {
      return ok;
    }

    public String getLicense() {
      return nullToEmpty(license);
    }

    public String getStatus() {
      return nullToEmpty(status);
    }

    public String getMessage() {
      return nullToEmpty(message);
    }

    private String nullToEmpty(String s) {
      return StringLegalizer.create(s).nullToEmpty().end();
    }
  }
}
