package de.fornalik.tankschlau.webserviceapi;

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * We only test for concrete implementation specific values here. All other behaviour should
 * be directly tested in unit tests for {@link de.fornalik.tankschlau.net.BaseRequest}
 */
class TankerkoenigRequestTest {
  private Geo geoFixture;

  @BeforeEach
  void setUp() {
    this.geoFixture = new Geo(53.1234, 48.5678, 12.0);
  }

  @Test
  void create_constructsProperValues() throws MalformedURLException {
    // when
    TankerkoenigRequest actualRequest = TankerkoenigRequest.create(geoFixture);

    // then
    assertEquals(
        "https://creativecommons.tankerkoenig.de/json/list.php?",
        actualRequest.getBaseUrl().toString());

    assertEquals(Request.HttpMethod.GET, actualRequest.getHttpMethod());

    assertEquals("application/json; charset=utf-8", actualRequest.getHeaders().get("Accept"));

    assertEquals(geoFixture.latitude, Double.valueOf(actualRequest.getUrlParameters().get("lat")));

    assertEquals(geoFixture.longitude, Double.valueOf(actualRequest.getUrlParameters().get("lng")));

    assertEquals(
        geoFixture.getDistance(),
        Optional.of(Double.valueOf(actualRequest.getUrlParameters().get("rad"))));
  }

  @Test
  void create_throwsOnMissingDistance() {
    // given
    geoFixture.setDistance(null);

    // when then
    assertThrows(
        TankerkoenigRequest.SearchRadiusException.class,
        () -> TankerkoenigRequest.create(geoFixture));
  }
}