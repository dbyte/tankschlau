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

package de.fornalik.tankschlau.webserviceapi.common;

import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.util.StringLegalizer;

import java.io.IOException;
import java.util.Optional;

public interface GeocodingClient {

  /**
   * Implementation should call a geocoding webservice with the provided {@link Address} data,
   * getting back latitude & longitude, wrapped in a {@link Geo} instance.
   *
   * @param address The Address which lacks of latitude & longitude.
   * @return Latitude & longitude data, wrapped in a {@link Geo} instance on successful
   * interaction with the geocoding service, else an empty Optional.
   * @throws IOException If something went wrong while reading the response of the webservice or
   *                     while requesting the server.
   */
  Optional<Geo> getGeo(Address address) throws IOException;

  /**
   * @return Some technical response data - sort of response summary or header data,
   * depending on the concrete implementation.
   */
  TransactionInfo getTransactionInfo();

  /**
   * @return Licence string.
   * @implSpec Implement according to provider's terms of use!
   */
  String getLicenseString();

  /**
   * Provides some technical response data - sort of response summary or header data,
   * depending on the concrete implementation.
   */
  @SuppressWarnings("unused")
  class TransactionInfo {
    @SerializedName("status") private String status;
    @SerializedName("error_message") private String message;
    @SerializedName("location_type") private String locationType;

    public String getStatus() {
      return nullToEmpty(status);
    }

    public void setStatus(String status) {
      this.status = status;
    }

    public String getMessage() {
      return nullToEmpty(message);
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public String getLocationType() {
      return nullToEmpty(locationType);
    }

    public void setLocationType(String locationType) {
      this.locationType = locationType;
    }

    private String nullToEmpty(String s) {
      return StringLegalizer.create(s).nullToEmpty().end();
    }
  }
}
