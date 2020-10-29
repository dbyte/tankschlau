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

import de.fornalik.tankschlau.service.HasTransactionInfo;

/**
 * The HTTP response interface used by this application.
 */
public interface Response extends HasTransactionInfo {

  /**
   * Gets the final response body, while the type T of its <code>data</code> field is determined
   * at runtime.
   *
   * @return Some implementation instance of a {@link ResponseBody}.
   */
  ResponseBody getBody();

  /**
   * Deeply recycles instance to default values. No new instance is created,
   * the old one is retained.
   *
   * @implSpec Implementations MUST RETAIN the same instance.
   */
  void reset();
}
