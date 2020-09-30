package de.fornalik.tankschlau.webserviceapi;

import de.fornalik.tankschlau.geo.Geo;
import de.fornalik.tankschlau.net.Request;
import de.fornalik.tankschlau.webserviceapi.tankerkoenig.TankerkoenigRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.MalformedURLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * We only test for concrete implementation specific values here. All other behaviour should
 * be directly tested in unit tests for {@link de.fornalik.tankschlau.net.BaseRequest}
 */
class TankerkoenigRequestTest {
  private ApiKeyManager apiKeyManagerMock;
  private Geo geoFixture;

  @BeforeEach
  void setUp() {
    this.apiKeyManagerMock = Mockito.mock(ApiKeyManager.class);
    this.geoFixture = new Geo(53.1234, 48.5678, 12.0);

    Mockito.when(apiKeyManagerMock.read()).thenReturn(Optional.of("000-abc-def-111"));
  }

  @Test
  void create_constructsProperValues() throws MalformedURLException {
    // given
    assert apiKeyManagerMock.read().isPresent(); // pre-check proper test setup

    // when
    TankerkoenigRequest actualRequest = TankerkoenigRequest.create(apiKeyManagerMock, geoFixture);

    // then
    assertEquals(
        "https://creativecommons.tankerkoenig.de/json/list.php?",
        actualRequest.getBaseUrl().toString());

    assertEquals(Request.HttpMethod.GET, actualRequest.getHttpMethod());

    assertEquals("application/json; charset=utf-8", actualRequest.getHeaders().get("Accept"));

    assertEquals("dist", actualRequest.getUrlParameters().get("sort"));
    assertEquals("all", actualRequest.getUrlParameters().get("type"));
    assertEquals(apiKeyManagerMock.read().get(), actualRequest.getUrlParameters().get("apikey"));

    assertEquals(
        geoFixture.getLatitude(),
        Double.valueOf(actualRequest.getUrlParameters().get("lat")));

    assertEquals(
        geoFixture.getLongitude(),
        Double.valueOf(actualRequest.getUrlParameters().get("lng")));

    assertEquals(
        geoFixture.getDistance(),
        Optional.of(Double.valueOf(actualRequest.getUrlParameters().get("rad"))));
  }

  @Test
  void create_throwsOnNullApiKeyManager() {
    // when then
    assertThrows(
        NullPointerException.class,
        () -> TankerkoenigRequest.create(null, geoFixture));
  }

  @Test
  void create_throwsOnMissingDistance() {
    // given
    geoFixture.setDistance(null);

    // when then
    assertThrows(
        TankerkoenigRequest.SearchRadiusException.class,
        () -> TankerkoenigRequest.create(apiKeyManagerMock, geoFixture));
  }
}