package de.fornalik.tankschlau.station;

import com.google.gson.JsonObject;
import de.fornalik.tankschlau.helpers.response.FixtureFiles;
import de.fornalik.tankschlau.helpers.response.JsonResponseFixture;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    JsonResponseFixture fixture = fixtures.getLeft();
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

    JsonResponseFixture fixture = fixtures.getLeft();
    JsonObject jsonStationFix = fixtures.getRight();

    // when
    Set<Petrol> actualPetrols = Petrols.createFromJson(jsonStationFix);

    // then
    // Expect that only 1 Petrol was created, because 2 of them have a 0.0 price JSON.
    assertEquals(1, actualPetrols.size());
  }

  // endregion

  @Test
  void getAsSortedListByPetrolType() {
  }

  @Test
  void getPetrol() {
  }

}