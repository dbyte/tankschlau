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

package de.fornalik.tankschlau.net;

import java.util.Optional;

/**
 * Implementation for a server's {@link Response} which provide a somewhat textual format.
 * Might be JSON, for example.
 */
public class StringResponse implements Response {
  private String body;
  private String errorMessage;

  private StringResponse() {}

  /**
   * Default factory. Creates a ready-for-use Response.
   */
  public static StringResponse create() {
    return new StringResponse();
  }

  @Override
  @SuppressWarnings("unchecked")
  public Optional<String> getBody() {
    return Optional.ofNullable(body);
  }

  @Override
  public <T> void setBody(T data) {
    if (!(data instanceof String))
      throw new ClassCastException("Data must be of type String.");

    this.body = (String) data;
  }

  @Override
  public Optional<String> getErrorMessage() {
    return Optional.ofNullable(errorMessage);
  }

  @Override
  public void setErrorMessage(String in) {
    this.errorMessage = in;
  }
}
