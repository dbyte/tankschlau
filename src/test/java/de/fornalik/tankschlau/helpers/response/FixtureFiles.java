package de.fornalik.tankschlau.helpers.response;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Utility class for test-fixture file handling.
 */
public class FixtureFiles {
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

  private FixtureFiles() {
  }

  /**
   * @param name String which represents a resource, separated by "/" as defined
   *             in {@link ClassLoader#getResource(String)}. Note that the implicit root of the
   *             final resource url is directory "test/resources".
   * @return A {@link FileReader} instance, linked to the resource if resource was found, else
   * throws RuntimeException. May also throw RTE if resource was not found.
   */
  public static FileReader getFileReaderForResource(String name) {
    Objects.requireNonNull(name);

    ClassLoader loader = JsonResponseFixture.class.getClassLoader();
    URL fileUrl = loader.getResource(name);

    if (fileUrl == null)
      throw new RuntimeException("Resource " + name + " not found for ClassLoader " + loader.toString());

    try {
      return new FileReader(fileUrl.getFile());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
