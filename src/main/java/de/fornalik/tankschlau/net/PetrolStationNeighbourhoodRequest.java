package de.fornalik.tankschlau.net;

import de.fornalik.tankschlau.geo.Geo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class PetrolStationNeighbourhoodRequest extends BaseRequest {

  private static final String BASE_URL = "https://creativecommons.tankerkoenig.de/json/list.php?";
  private static final String HTTP_METHOD = "GET";
  private static final String ACCEPT_JSON = "application/json; charset=utf-8";
  private Geo geo;

  public PetrolStationNeighbourhoodRequest() {
    geo = null;
  }

  public static PetrolStationNeighbourhoodRequest create(
      double userLat,
      double userLng,
      double maxDistance,
      String apiKey) throws MalformedURLException {

    PetrolStationNeighbourhoodRequest instance = new PetrolStationNeighbourhoodRequest();

    instance.geo = new Geo(userLat, userLng, maxDistance);
    instance.setBaseUrl(new URL(BASE_URL));
    instance.setMethod(HTTP_METHOD);
    instance.addHeader("Accept", ACCEPT_JSON);

    instance.addUrlParameter("lat", Double.valueOf(userLat).toString());
    instance.addUrlParameter("lng", Double.valueOf(userLng).toString());
    instance.addUrlParameter("rad", Double.valueOf(maxDistance).toString());
    instance.addUrlParameter("sort", "dist");
    instance.addUrlParameter("type", "all");
    instance.addUrlParameter("apikey", apiKey);

    return instance;
  }

  public void setGeo(Geo geo) {
    this.geo = Objects.requireNonNull(geo, "Geo data must not be null.");
  }
}
