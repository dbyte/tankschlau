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
 * Abstract base class for a server's {@link Response} which provides body data
 * and an optional error message.
 */
abstract class BaseResponse<T> implements Response<T> {
  private T body;
  private String errorMessage;

  @Override
  public Optional<T> getBody() {
    return Optional.ofNullable(body);
  }

  @Override
  public void setBody(T data) {
    this.body = data;
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
