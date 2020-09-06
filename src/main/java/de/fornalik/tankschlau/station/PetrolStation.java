package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Distance;
import de.fornalik.tankschlau.geo.GeoLocation;

import java.util.*;

public class PetrolStation {
  public final UUID uuid;
  public final String name;
  public final String brand;
  public final String place;
  public final GeoLocation geoLocation;
  public final Distance distance;
  private ArrayList<Petrol> petrols;

  public PetrolStation(
      UUID uuid,
      String name,
      String brand,
      String place,
      GeoLocation geoLocation,
      Distance distance,
      ArrayList<Petrol> petrols) {
    this.uuid = uuid;
    this.name = name;
    this.brand = brand;
    this.place = place;
    this.geoLocation = geoLocation;
    this.distance = distance;
    setPetrols(petrols);
  }

  public ArrayList<Petrol> getPetrols() {
    return petrols;
  }

  public void setPetrols(ArrayList<Petrol> petrols) {
    this.petrols = petrols != null ? petrols : new ArrayList<>();
  }

  public double getPrice(PetrolType petrolType) throws PriceException {
    double[] priceValues = petrols.stream()
        .filter(price -> price.type.equals(petrolType))
        .mapToDouble(petrol -> petrol.price).toArray();

    throwOnInvalidPrice(petrolType, priceValues.length);
    return priceValues[0];
  }

  private void throwOnInvalidPrice(PetrolType petrolType, int countResults) throws PriceException {
    if (countResults == 0) {
      throw new PriceException(String.format("No price data for petrol type %s", petrolType));

    } else if (countResults >= 2) {
      throw new PriceException(
          String.format("Price has %d duplicates for petrol type %s", countResults, petrolType));
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    PetrolStation that = (PetrolStation) o;
    boolean isEqual = Objects.equals(uuid, that.uuid) &&
        Objects.equals(name, that.name) &&
        Objects.equals(brand, that.brand) &&
        Objects.equals(place, that.place) &&
        Objects.equals(geoLocation, that.geoLocation) &&
        Objects.equals(distance, that.distance);
    if (!isEqual) return false;

    // Expensive check, thus at the end
    this.getPetrols().sort(Petrol::compareTo);
    that.getPetrols().sort(Petrol::compareTo);
    return Arrays.equals(getPetrols().toArray(), that.getPetrols().toArray());
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", PetrolStation.class.getSimpleName() + "[", "]")
        .add("uuid=" + uuid)
        .add("name='" + name + "'")
        .add("brand='" + brand + "'")
        .add("place='" + place + "'")
        .add("geoLocation=" + geoLocation)
        .add("distanceToCurrentLocation=" + distance)
        .add("petrols=" + (petrols != null ? petrols.toString() : null))
        .toString();
  }

  public static class PriceException extends Exception {
    public PriceException(String message) {
      super(message);
    }
  }
}
