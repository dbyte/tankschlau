package de.fornalik.tankschlau;

import de.fornalik.tankschlau.geo.Distance;
import de.fornalik.tankschlau.geo.GeoLocation;
import de.fornalik.tankschlau.station.Petrol;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolType;

import java.util.ArrayList;
import java.util.UUID;

public class TankSchlau {

  public static void main(String[] args) {
    GeoLocation location = new GeoLocation("Meinestraße", "42", "38440");

    ArrayList<Petrol> petrols = new ArrayList<>();
    petrols.add(new Petrol(PetrolType.DIESEL, 1.00));
    petrols.add(new Petrol(PetrolType.E5, 1.30));
    petrols.add(new Petrol(PetrolType.E10, 1.40));

    Distance distance = new Distance(2.56);

    PetrolStation station = new PetrolStation(new UUID(234, 123),
                                              "Esso WOB",
                                              "Esso",
                                              "Wolfsburg Innenstadt",
                                              location,
                                              distance,
                                              petrols);


    try {
      double price = station.getPrice(PetrolType.E5);
      System.out.println(price);

    } catch (PetrolStation.PriceException e) {
      System.err.println(e.getMessage());
    }
  }
}
