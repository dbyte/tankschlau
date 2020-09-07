package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Address;

import java.util.UUID;

/**
 * PetrolStation factory for test fixtures.
 */
public class PetrolStationFixture {

  public static PetrolStation create_Berlin() {
    return PetrolStationBuilder.init()
        .setBrand("TOTAL")
        .addPetrol(PetrolType.DIESEL, 1.109)
        .addPetrol(PetrolType.E10, 1.319)
        .addPetrol(PetrolType.E5, 1.339)
        .setDistance(1.1)
        .setAddress(Address.Builder.init()
                        .setName("TOTAL BERLIN")
                        .setMandatoryFields("MARGARETE-SOMMER-STR.", "BERLIN", "10407")
                        .setHouseNumber("2")
                        .setCoordinates2D(52.53083, 13.440946)
                        .build())
        .build(UUID.fromString("474e5046-deaf-4f9b-9a32-9797b778f047"));
  }
}
