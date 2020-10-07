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

/**
 * Abstract HTTP request class with base functionalities and <b>additional hook</b> for generating
 * proper request parameters for a push message.
 */
// TODO unit tests
public abstract class PushMessageRequest extends BaseRequest {

  /**
   * Implementation should set or overwrite the "message" for this request.
   *
   * @param message The message string itself. Empty or null values should be allowed here by
   *                each implementation.
   * @throws NullPointerException If given message string is null.
   */
  public abstract void setMessage(String message);
}
