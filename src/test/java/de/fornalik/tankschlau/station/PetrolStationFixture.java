package de.fornalik.tankschlau.station;

import java.util.UUID;

public class PetrolStationFixture {

  public static PetrolStation create_Berlin() {
    return new PetrolStationBuilder()
        .setBaseData("TOTAL BERLIN", "TOTAL", "BERLIN")
        .addPetrol(PetrolType.DIESEL, 1.109)
        .addPetrol(PetrolType.E10, 1.319)
        .addPetrol(PetrolType.E5, 1.339)
        .setDistance(1.1)
        .setAddress("MARGARETE-SOMMER-STR.", "2", "10407")
        .setCoordinates(52.53083, 13.440946)
        .create(UUID.fromString("474e5046-deaf-4f9b-9a32-9797b778f047"));
  }
}
