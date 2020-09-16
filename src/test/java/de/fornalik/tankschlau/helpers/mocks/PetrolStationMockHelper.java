package de.fornalik.tankschlau.helpers.mocks;

import de.fornalik.tankschlau.geo.Address;
import de.fornalik.tankschlau.station.Petrol;
import de.fornalik.tankschlau.station.PetrolStation;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.Mockito.mock;

public class PetrolStationMockHelper {

  public final UUID uuidFixture;
  public final Address addressMock;
  public final Petrol petrolMock1, petrolMock2;
  public final Set<Petrol> petrolsFixture;
  public PetrolStation petrolStationMock;

  public PetrolStationMockHelper() {
    this.uuidFixture = UUID.fromString("fb48f03e-c16e-435d-a845-6e235612f88f");

    this.addressMock = mock(Address.class);

    this.petrolMock1 = mock(Petrol.class);
    this.petrolMock2 = mock(Petrol.class);

    this.petrolsFixture = new HashSet<>();
    petrolsFixture.add(petrolMock1);
    petrolsFixture.add(petrolMock2);

    this.petrolStationMock = mock(PetrolStation.class);
  }
}
