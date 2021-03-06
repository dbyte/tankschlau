/*
 * Copyright (c) 2020 Tammo Fornalik
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.fornalik.tankschlau.testhelp_common;

/**
 * Utility class for test-fixture file handling.
 */
public class FixtureFiles {

  // region Tankerkoenig petrol stations responses

  public static final String TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR =
      "response_fixtures/tankerkoenig/neighbourhood/json/";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_HAPPY =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "OneHappyStation.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_1STATION_MISSING_ID_ELEM =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "MissingIdElem.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_STATIONS_ELEM =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "ResponseOkButMissingStations.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_STATION_ARRAY =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "ResponseOkButEmptyStations.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_HOUSENUM_AND_BRAND =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "EmptyHouseNumberAndBrand.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_DIST_ELEM =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "MissingDistanceElement.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_LAT_LON_ELEM =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "MissingLatLonElements.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_ALL_GEO_ELEM =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "MissingAllGeoElements.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_EMPTY_STREET_AND_PLACE_AND_POSTCODE =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "EmptyStreetAndPlaceAndPostCode.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_17STATIONS_HAPPY =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "17HappyStations.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MULTI_34STATIONS_HAPPY =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "34HappyStations.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_COMPARE_TWO_1ST_IS_CHEAPEST =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "Compare2Stations_FirstIsCheapest.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_COMPARE_TWO_2ND_IS_CHEAPER_AND_FURTHER =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR
          + "Compare2Stations_SecondIsCheaperAndFurtherAway.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_COMPARE_TWO_EQUAL_PRICES_BUT_2ND_IS_CLOSER =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "Compare2Stations_EqualPricesBut2ndIsCloser"
          + ".json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_MISSING_DIESEL_AND_E5 =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "MissingPriceForDieselAndE5.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_ZERO_PRICE_DIESEL_AND_E10 =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "ZeroPriceForDieselAndE10.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_STATIONS_ARRAY_IS_STRING_ARRAY =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR +
          "StationsArrayWithJsonStringsInsteadOfJsonObjects.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_MISSING_APIKEY =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "MissingApiKey.json";

  public static final String TANKERKOENIG_JSON_RESPONSE_LONGITUDE_ERROR =
      TANKERKOENIG_NEIGHBOURHOOD_JSON_RESPONSE_DIR + "LongitudeOutOfBoundsError.json";

  // endregion
  // --------------------------------------------------------------------------
  // region Google Geocoding responses

  public static final String GOOGLE_GEO_RESPONSE_DIR =
      "response_fixtures/google/geocoding/";

  public static final String GOOGLE_GEO_RESPONSE_50_1078234_8_5413809_Rooftop =
      GOOGLE_GEO_RESPONSE_DIR + "50_1078234_8_5413809_Rooftop.json";

  public static final String GOOGLE_GEO_RESPONSE_52_39097_10_84663_Rooftop =
      GOOGLE_GEO_RESPONSE_DIR + "52_39097_10_84663_Rooftop.json";

  public static final String GOOGLE_GEO_RESPONSE_52_5006049_13_3136007_GeometricCenter =
      GOOGLE_GEO_RESPONSE_DIR + "52_5006049_13_3136007_GeometricCenter.json";

  public static final String GOOGLE_GEO_RESPONSE_52_9541353_8_2396026_Approximate =
      GOOGLE_GEO_RESPONSE_DIR + "52_9541353_8_2396026_Approximate.json";

  public static final String GOOGLE_GEO_RESPONSE_MissingApiKey =
      GOOGLE_GEO_RESPONSE_DIR + "MissingApiKey.json";

  public static final String GOOGLE_GEO_RESPONSE_ZeroResults =
      GOOGLE_GEO_RESPONSE_DIR + "ZeroResults.json";

  // endregion
  // --------------------------------------------------------------------------
  // region Pushover Message Responses

  public static final String PUSHOVER_RESPONSE_DIR = "response_fixtures/pushmessage/pushover/";

  public static final String PUSHOVER_RESPONSE_STATUS_1 =
      PUSHOVER_RESPONSE_DIR + "Status_1.json";

  public static final String PUSHOVER_RESPONSE_STATUS_0_INVALID_TOKEN =
      PUSHOVER_RESPONSE_DIR + "Status_0_InvalidToken.json";

  public static final String PUSHOVER_RESPONSE_STATUS_0_INVALID_SECRET =
      PUSHOVER_RESPONSE_DIR + "Status_0_InvalidSecret.json";

  public static final String PUSHOVER_RESPONSE_STATUS_0_INVALID_USER =
      PUSHOVER_RESPONSE_DIR + "Status_0_InvalidUser.json";

  // endregion

  // --------------------------------------------------------------------------

  private FixtureFiles() {
  }
}
