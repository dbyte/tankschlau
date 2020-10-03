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

import com.google.gson.annotations.SerializedName;
import de.fornalik.tankschlau.station.PetrolStation;

import java.util.List;

/**
 * DataTransferObject for tankerkoenig.de response.
 * This is the target for JSON adapters which convert the response body to our ORM.
 */
public class TankerkoenigPetrolStationsDao {
  private TransactionInfo transactionInfo;

  public TankerkoenigPetrolStationsDao() {
    this.transactionInfo = new TransactionInfo();
  }

  public TransactionInfo getTransactionInfo() {
    return transactionInfo;
  }

  public static class TransactionInfo {
    @SerializedName("ok") private boolean ok;
    @SerializedName("license") private String license;
    @SerializedName("status") private String status;
    @SerializedName("stations") private List<PetrolStation> petrolStations;

    public boolean isOk() {
      return ok;
    }

    public String getLicense() {
      return license;
    }

    public String getStatus() {
      return status;
    }

    public List<PetrolStation> getPetrolStations() {
      return petrolStations;
    }
  }
}
