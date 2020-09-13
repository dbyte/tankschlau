package de.fornalik.tankschlau.geo;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Distance {
  @SerializedName("dist") private double km;

  public Distance(double km) {
    setKm(km);
  }

  public static Distance createFromJson(JsonObject in) {
    return new Gson().fromJson(in, Distance.class);
  }

  public double getKm() {
    return km;
  }

  public void setKm(double km) throws IllegalArgumentException {
    if (km < 0.0) throw new IllegalArgumentException("Distance must be >= 0.0");
    this.km = km;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Distance distance = (Distance) o;

    return new EqualsBuilder()
        .append(getKm(), distance.getKm())
        .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(19, 25)
        .append(getKm())
        .toHashCode();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("km", km)
        .toString();
  }

}
