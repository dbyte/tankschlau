package de.fornalik.tankschlau.station;

import de.fornalik.tankschlau.geo.Distance;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;
import java.util.UUID;

/**
 * PetrolStation factory for test thiss.
 * <p>
 * Use for creating fixed data of a {@link PetrolStation}.
 * All fields are public mutable for testing purposes. Also, all primitives are wrapped
 * to be able to null them for testing purposes.
 */
public class PetrolStationFixture {
  public UUID uuid;
  public String name;
  public String brand;
  public boolean isOpen;
  public String street;
  public String houseNumber;
  public String city;
  public String postCode;
  public Double lat;
  public Double lng;
  public Double distanceKm;
  public Double priceDiesel;
  public Double priceE5;
  public Double priceE10;

  private PetrolStationFixture() {
  }

  public static PetrolStationFixture newInstance() {
    return new PetrolStationFixture();
  }

  public PetrolStationFixture create_Berlin() {
    uuid = UUID.fromString("474e5046-deaf-4f9b-9a32-9797b778f047");
    name = "TOTAL BERLIN";
    brand = "TOTAL";
    isOpen = true;
    street = "MARGARETE-SOMMER-STR.";
    houseNumber = "2";
    city = "BERLIN";
    postCode = "10407";
    lat = 52.53083;
    lng = 13.440946;
    distanceKm = 1.1;
    priceDiesel = 1.109;
    priceE5 = 1.339;
    priceE10 = 1.319;

    return this;
  }

  public void assertEquals(PetrolStation petrolStation) {
    Assertions.assertNotNull(petrolStation);

    Assertions.assertEquals(uuid.toString(), petrolStation.uuid.toString());
    Assertions.assertEquals(brand, petrolStation.brand);
    Assertions.assertEquals(isOpen, petrolStation.isOpen);

    Assertions.assertEquals(Optional.of(distanceKm),
                            petrolStation.getDistance().map(Distance::getKm));

    Assertions.assertNotNull(petrolStation.address);
    Assertions.assertEquals(name, petrolStation.address.getName());
    Assertions.assertEquals(street, petrolStation.address.getStreet());
    Assertions.assertEquals(houseNumber, petrolStation.address.getHouseNumber());
    Assertions.assertEquals(city, petrolStation.address.getCity());
    Assertions.assertEquals(postCode, petrolStation.address.getPostCode());

    Assertions.assertEquals(Optional.ofNullable(lat),
                            petrolStation.address.getCoordinates2D().map(c -> c.latitude));

    Assertions.assertEquals(Optional.ofNullable(lng),
                            petrolStation.address.getCoordinates2D().map(c -> c.longitude));

    Assertions.assertEquals(Optional.ofNullable(priceDiesel),
                            petrolStation.getPetrolPrice(PetrolType.DIESEL));

    Assertions.assertEquals(Optional.ofNullable(priceE10),
                            petrolStation.getPetrolPrice(PetrolType.E10));

    Assertions.assertEquals(Optional.ofNullable(priceE5),
                            petrolStation.getPetrolPrice(PetrolType.E5));
  }
}
