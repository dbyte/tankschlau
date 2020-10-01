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

import de.fornalik.tankschlau.util.MyToStringBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Objects;

public class Petrol {
  public final PetrolType type;
  public final double price;

  public Petrol(PetrolType type, double price) {
    this.type = Objects.requireNonNull(type, "type must not be null.");
    this.price = price;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Petrol petrol = (Petrol) o;

    return new EqualsBuilder()
        .append(price, petrol.price)
        .append(type, petrol.type)
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(13, 61)
        .append(type)
        .append(price)
        .toHashCode();
  }

  @Override
  public String toString() {
    return new MyToStringBuilder(this)
        .append("type", type)
        .append("price", price)
        .toString();
  }
}
