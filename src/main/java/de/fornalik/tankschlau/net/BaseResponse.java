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

import de.fornalik.tankschlau.storage.TransactInfo;

import java.util.Optional;

/**
 * Base class for a server's {@link Response} which provides body data
 * and an optional error message.
 */
public class BaseResponse implements Response {
  private final ResponseBody responseBody;
  private final TransactInfo transactInfo;

  public BaseResponse(ResponseBody responseBody, TransactInfo transactInfo) {
    this.responseBody = responseBody;
    this.transactInfo = transactInfo;
  }

  @Override
  public Optional<ResponseBody> getBody() {
    return Optional.ofNullable(responseBody);
  }

  @Override
  public TransactInfo getTransactInfo() {
    return transactInfo;
  }

  @Override
  public Response reset() {
    responseBody.setData(null);
    transactInfo.reset();
    return this;
  }
}
