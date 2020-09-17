package de.fornalik.tankschlau.net;

import java.io.IOException;
import java.util.Optional;

/**
 * Interface for HTTP client used by this application.
 */
public interface HttpClient {

  /**
   * @return The request which was last used invoking {@link #newCall(Request, Response)}
   */
  Optional<Request> getRequest();

  /**
   * @param request  A configured {@link Request} object.
   * @param response A default-initialized {@link Response} object, which will be populated by the
   *                 server's response data, then being passed back as the return value.
   * @return The populated {@link Response} object which was passed to this method.
   * @throws IOException If something went wrong while handling communication etc.
   */
  Response newCall(Request request, Response response) throws IOException;
}
