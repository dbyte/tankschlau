package de.fornalik.tankschlau.helpers.response;

import de.fornalik.tankschlau.station.PetrolStationFixture;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Utility class for test-fixture file handling.
 */
public class FixtureFiles {
  public static final String TANKERKOENIG_JSON_RESPONSE_DIR =
      "response_fixtures/tankerkoenig/json/";

  public static final String TANKERKOENIG_JSON_RESPONSE_NEIGHBOURHOOD_ONE_HAPPY_STATION =
      TANKERKOENIG_JSON_RESPONSE_DIR + "TankerkoenigNeighbourhoodResponse_OneHappyStation.json";

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

    ClassLoader loader = PetrolStationFixture.class.getClassLoader();
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
