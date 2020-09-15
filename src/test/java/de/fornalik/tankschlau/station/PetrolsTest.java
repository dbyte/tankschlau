package de.fornalik.tankschlau.station;

import com.google.gson.JsonObject;
import de.fornalik.tankschlau.helpers.response.FixtureFiles;
import de.fornalik.tankschlau.helpers.response.JsonResponseFixture;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class PetrolsTest {

  // region createFromJson Tests
  /*
  The underlying implementation of this factory method is subject to the corresponding
  adapter unit, so we just do a basic happy path test here.
  */

  @Test
  void createFromJson_doesCreateAllPetrols() {
    // given
    Pair<JsonResponseFixture, JsonObject> fixtures =
        JsonResponseFixture.createFirstStationFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY);

    JsonResponseFixture fixture = fixtures.getLeft();
    JsonObject jsonStationFix = fixtures.getRight();

    // when
    Set<Petrol> actualPetrols = Petrols.createFromJson(jsonStationFix);

    // then
    fixture.assertEquals(actualPetrols, 0);
  }

  @Test
  void createFromJson_doesNotCreatePetrolsForMissingPrices() {
    // given
    Pair<JsonResponseFixture, JsonObject> fixtures =
        JsonResponseFixture.createFirstStationFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_DIESEL_AND_E5);

    JsonObject jsonStationFix = fixtures.getRight();

    // when
    Set<Petrol> actualPetrols = Petrols.createFromJson(jsonStationFix);

    // then
    // Expect that only 1 Petrol was created, because 2 of them miss their price in JSON.
    assertEquals(1, actualPetrols.size());
  }

  @Test
  void createFromJson_doesNotCreatePetrolsWithZeroPrice() {
    // given
    Pair<JsonResponseFixture, JsonObject> fixtures =
        JsonResponseFixture.createFirstStationFromJsonFile(
            FixtureFiles.TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_ZERO_PRICE_DIESEL_AND_E10);

    JsonObject jsonStationFix = fixtures.getRight();

    // when
    Set<Petrol> actualPetrols = Petrols.createFromJson(jsonStationFix);

    // then
    // Expect that only 1 Petrol was created, because 2 of them have a 0.0 price JSON.
    assertEquals(1, actualPetrols.size());
  }

  // endregion

  @Test
  void getSortedByPetrolTypeAndPrice_sortsByOrderOfPetrolTypeCasesAndThenPrice() {
    // given

    // Expected order definition : 1st sorted by order of PetrolType definitions, then by price
    ArrayList<Petrol> expectedSortedPetrols = new ArrayList<>();
    expectedSortedPetrols.add(new Petrol(PetrolType.DIESEL, 0.078));
    expectedSortedPetrols.add(new Petrol(PetrolType.DIESEL, 5.459));
    expectedSortedPetrols.add(new Petrol(PetrolType.E5, 1.789));
    expectedSortedPetrols.add(new Petrol(PetrolType.E5, 2.567));
    expectedSortedPetrols.add(new Petrol(PetrolType.E5, 3.234));
    expectedSortedPetrols.add(new Petrol(PetrolType.E10, 1.450));
    expectedSortedPetrols.add(new Petrol(PetrolType.E10, 1.496));

    Set<Petrol> unsortedPetrols = new HashSet<>();
    unsortedPetrols.add(new Petrol(PetrolType.E5, 3.234));
    unsortedPetrols.add(new Petrol(PetrolType.DIESEL, 5.459));
    unsortedPetrols.add(new Petrol(PetrolType.E10, 1.496));
    unsortedPetrols.add(new Petrol(PetrolType.E5, 2.567));
    unsortedPetrols.add(new Petrol(PetrolType.DIESEL, 0.078));
    unsortedPetrols.add(new Petrol(PetrolType.E5, 1.789));
    unsortedPetrols.add(new Petrol(PetrolType.E10, 1.450));

    // when
    ArrayList<Petrol> actualSortedPetrols =
        (ArrayList<Petrol>) Petrols.getSortedByPetrolTypeAndPrice(
            unsortedPetrols);

    // then
    // Expected order: 1st sorted by order of PetrolType definitions, then by price
    assertIterableEquals(expectedSortedPetrols, actualSortedPetrols);
  }

  @Test
  void getSortedByPetrolTypeAndPrice_returnsEmptyListIfNullSetIsPassed() {
    // when
    List<Petrol> actualSortedPetrols = Petrols.getSortedByPetrolTypeAndPrice(null);

    // then
    assertNotNull(actualSortedPetrols);
    assertEquals(0, actualSortedPetrols.size());
  }

  @ParameterizedTest
  @CsvSource(value = {
      "DIESEL; 0.997",
      "E10; 1.229",
      "E5; 1.05"},
      delimiter = ';')
  void findPetrol_happy(String expectedPetrolTypeStr, double expectedPrice) {
    // given
    PetrolType expectedPetrolType = PetrolType.valueOf(expectedPetrolTypeStr);
    Petrol expectedPetrol = new Petrol(expectedPetrolType, expectedPrice);

    HashSet<Petrol> givenPetrols = new HashSet<>();
    givenPetrols.add(new Petrol(PetrolType.DIESEL, 0.997));
    givenPetrols.add(new Petrol(PetrolType.E10, 1.229));
    givenPetrols.add(new Petrol(PetrolType.E5, 1.05));

    // when
    Optional<Petrol> foundPetrol = Petrols.findPetrol(givenPetrols, expectedPetrolType);

    // then
    // noinspection OptionalGetWithoutIsPresent
    assertSame(expectedPetrol.type, foundPetrol.get().type);
    assertEquals(expectedPetrol.price, foundPetrol.get().price);
  }

  @ParameterizedTest
  @CsvSource(value = {"E10", "E5"}, delimiter = ';')
  void findPetrol_ReturnsEmptyOptionalIfNotFound(String unexpectedPetrolTypeStr) {
    // given
    PetrolType expectedPetrolType = PetrolType.valueOf(unexpectedPetrolTypeStr);

    HashSet<Petrol> givenPetrols = new HashSet<>();
    givenPetrols.add(new Petrol(PetrolType.DIESEL, 1.11));

    // when
    Optional<Petrol> foundPetrol = Petrols.findPetrol(givenPetrols, expectedPetrolType);

    // then
    assertEquals(Optional.empty(), foundPetrol);
  }

  @Test
  void findPetrol_ReturnsEmptyOptionalIfAnyGivenArgumentIsNull() {
    // given
    HashSet<Petrol> givenPetrols = new HashSet<>();
    givenPetrols.add(new Petrol(PetrolType.DIESEL, 1.11));
    // when
    Optional<Petrol> foundPetrol = Petrols.findPetrol(givenPetrols, null);
    // then
    assertEquals(Optional.empty(), foundPetrol);

    // given, when
    foundPetrol = Petrols.findPetrol(null, PetrolType.E5);
    // then
    assertEquals(Optional.empty(), foundPetrol);
  }

  @Test
  void findPetrol_throwsOnFoundDuplicatePetrolTypes() {
    // given
    Petrol unexpectedPetrol = new Petrol(PetrolType.E10, 2.22);

    HashSet<Petrol> givenPetrols = new HashSet<>();
    givenPetrols.add(new Petrol(PetrolType.E10, 2.22));
    givenPetrols.add(new Petrol(PetrolType.E10, 1.229));

    // when, then
    assertThrows(
        Petrols.PetrolsDuplicateException.class,
        () -> Petrols.findPetrol(givenPetrols, unexpectedPetrol.type));
  }

}