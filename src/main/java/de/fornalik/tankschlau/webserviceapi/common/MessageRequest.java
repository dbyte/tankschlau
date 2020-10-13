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

import de.fornalik.tankschlau.net.Request;

/**
 * Abstract HTTP request class with base functionalities and <b>additional hook</b> for generating
 * proper request parameters for a push message.
 */
public interface MessageRequest extends Request {

  /**
   * Implementation should set or overwrite the "message" parameter with the new value for this
   * request.
   *
   * @param content Content of the message (title, text etc.).
   */
  void setMessage(MessageContent content);
}
