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

/**
 * The HTTP response interface used by this application.
 */
public interface Response {

  /**
   * Gets the final response body, while the type T of its <code>data</code> field is determined
   * at runtime.
   *
   * @return Some implementation instance of a {@link ResponseBody}.
   */
  ResponseBody getBody();

  /**
   * @return Some implementation of a {@link TransactInfo}
   */
  TransactInfo getTransactInfo();

  /**
   * Recycles the response instance.
   *
   * @implSpec Implementations must RETAIN the instance. No "new" allowed by convention.
   */
  void reset();
}
