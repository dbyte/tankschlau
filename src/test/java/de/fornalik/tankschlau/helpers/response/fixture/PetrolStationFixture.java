package de.fornalik.tankschlau.helpers.response.fixture;

import de.fornalik.tankschlau.geo.Coordinates2D;
import de.fornalik.tankschlau.geo.Distance;
import de.fornalik.tankschlau.geo.GeoLocation;
import de.fornalik.tankschlau.station.Petrol;
import de.fornalik.tankschlau.station.PetrolStation;
import de.fornalik.tankschlau.station.PetrolType;

import java.util.ArrayList;
import java.util.UUID;

public class PetrolStationFixture {

  public static PetrolStation create_Berlin() {
    GeoLocation location = new GeoLocation(
        "MARGARETE-SOMMER-STR.",
        "2",
        "10407");
    location.setCoordinates2D(new Coordinates2D(52.53083, 13.440946));

    Distance distance = new Distance(1.1);

    ArrayList<Petrol> petrols = new ArrayList<>();
    petrols.add(new Petrol(PetrolType.DIESEL, 1.109));
    petrols.add(new Petrol(PetrolType.E5, 1.339));
    petrols.add(new Petrol(PetrolType.E10, 1.319));

    return new PetrolStation(
        UUID.fromString("474e5046-deaf-4f9b-9a32-9797b778f047"),
        "TOTAL BERLIN",
        "TOTAL",
        "BERLIN",
        location,
        distance,
        petrols);
  }
}
