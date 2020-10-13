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

package de.fornalik.tankschlau.webserviceapi.common;

import de.fornalik.tankschlau.net.Response;

public interface MessageClient {

  /**
   * Implementation should call a push message webservice with the provided
   * {@link MessageContent} data, getting back some response data about the transaction.
   *
   * @param content Describes the content (title, text, etc.) of a message.
   * @return {@link Response} data object with some info about the transaction.
   */
  Response sendMessage(MessageContent content);
}
