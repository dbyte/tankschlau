package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Distance;
import de.fornalik.tankschlau.geo.GeoLocation;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.UUID;

public class PetrolStation {
  private final UUID uuid;
  private final String name;
  private final String brand;
  private final String place;
  private final GeoLocation geoLocation;
  private Distance distanceToCurrentLocation;
  private ArrayList<Petrol> petrols;

  public PetrolStation(
      UUID uuid,
      String name,
      String brand,
      String place,
      GeoLocation geoLocation,
      Distance distanceToCurrentLocation,
      ArrayList<Petrol> petrols) {
    this.uuid = uuid;
    this.name = name;
    this.brand = brand;
    this.place = place;
    this.geoLocation = geoLocation;
    this.distanceToCurrentLocation = distanceToCurrentLocation;
    this.petrols = petrols;
  }

  public double getPrice(PetrolType petrolType) throws PriceException {
    double[] priceValues = petrols.stream()
        .filter(price -> price.getType().equals(petrolType))
        .mapToDouble(Petrol::getPrice).toArray();

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
  public String toString() {
    return new StringJoiner(", ", PetrolStation.class.getSimpleName() + "[", "]")
        .add("uuid=" + uuid)
        .add("name='" + name + "'")
        .add("brand='" + brand + "'")
        .add("place='" + place + "'")
        .add("geoLocation=" + geoLocation)
        .add("distanceToCurrentLocation=" + distanceToCurrentLocation)
        .add("petrols=" + petrols.toString())
        .toString();
  }

  public static class PriceException extends Exception {
    public PriceException(String message) {
      super(message);
    }
  }
}
