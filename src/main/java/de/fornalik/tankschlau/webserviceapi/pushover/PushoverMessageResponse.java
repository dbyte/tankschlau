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

package de.fornalik.tankschlau.webserviceapi.pushover;

import de.fornalik.tankschlau.net.BaseResponse;
import de.fornalik.tankschlau.net.JsonResponse;
import de.fornalik.tankschlau.net.ResponseBody;
import de.fornalik.tankschlau.storage.TransactInfo;

import java.util.Optional;

// TODO unit tests

/**
 * Implementation of {@link JsonResponse} for pushover.net webservice.
 *
 * @see
 * <a href="https://pushover.net/api#response">API response documentation: https://pushover.net/api#response</a>
 */
public class PushoverMessageResponse extends BaseResponse implements JsonResponse {

  public PushoverMessageResponse(ResponseBody responseBody, TransactInfo transactInfo) {

    super(responseBody, transactInfo);
  }

  @Override
  public <T> Optional<T> fromJson(String jsonString, Class<T> targetClass) {
    return Optional.empty();
  }
}
