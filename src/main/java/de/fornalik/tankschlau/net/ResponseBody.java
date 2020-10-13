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
 * Body of a server response.
 */
public interface ResponseBody {

  /**
   * @param ofType Type variable which determines the class of body data, e.g. <code>String
   *               .class</code>.
   * @param <T>    same
   * @return The data part of the response of the server.
   */
  <T> T getData(Class<T> ofType);

  /**
   * @param data The data part of the response of the server.
   * @param <T>  Class type  which determines the class of body data, e.g. <code>String
   *             .class</code>.
   */
  <T> void setData(T data);
}
