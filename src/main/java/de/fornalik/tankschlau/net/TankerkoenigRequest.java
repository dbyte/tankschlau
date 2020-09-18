package de.fornalik.tankschlau.net;

import de.fornalik.tankschlau.geo.Geo;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class TankerkoenigRequest extends BaseRequest {
  private static final String BASE_URL = "https://creativecommons.tankerkoenig.de/json/list.php?";
  private static final String HTTP_METHOD = "GET";
  private static final String ACCEPT_JSON = "application/json; charset=utf-8";
  private static final String DEMO_API_KEY = "00000000-0000-0000-0000-000000000002";
  private Geo geo;

  private TankerkoenigRequest() {}

  public static TankerkoenigRequest create(Geo geo) throws MalformedURLException {
    TankerkoenigRequest instance = new TankerkoenigRequest();

    instance.geo = Objects.requireNonNull(geo, "Geographical data (geo) must not be null.");
    instance.setBaseData();
    instance.setUrlParameters();

    return instance;
  }

  private void setBaseData() throws MalformedURLException {
    super.setBaseUrl(new URL(BASE_URL));
    super.setMethod(HTTP_METHOD);
    super.addHeader("Accept", ACCEPT_JSON);
  }

  private void setUrlParameters() {
    Double maxSearchRadius = geo.getDistance()
                                .orElseThrow(SearchRadiusException::new);

    super.addUrlParameter("lat", Double.valueOf(geo.latitude).toString());
    super.addUrlParameter("lng", Double.valueOf(geo.longitude).toString());
    super.addUrlParameter("rad", maxSearchRadius.toString());

    /* As we sort data ourselves, always request "all" petrol types. Per web service API definition
    of Tankerkoenig.de, "sort" must be set to "dist" when requesting "type" with "all". */
    super.addUrlParameter("sort", "dist");
    super.addUrlParameter("type", "all");

    super.addUrlParameter("apikey", DEMO_API_KEY);
  }

  public static class SearchRadiusException extends IllegalStateException {
    protected SearchRadiusException() {
      super(
          "Maximum distance radius (km), used for petrol station "
              + "search in your neighbourhood, must not be null.");
    }
  }
}
