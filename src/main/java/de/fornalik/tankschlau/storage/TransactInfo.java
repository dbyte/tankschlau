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

package de.fornalik.tankschlau.storage;

import java.util.Optional;

/**
 * Holds some kind of information of the last communication with a backend storage.
 */
public interface TransactInfo {

  /**
   * @return Some information about the client/server transaction, directly provided
   * by the server or enriched by ourselves. Should return empty string if no info is available.
   */
  String getStatus();

  /**
   * @param data Some information about the client/server transaction, directly provided
   *             by the server or enriched by ourselves.
   */
  void setStatus(String data);

  /**
   * @return Optional error message for errors which have not been thrown at request time.
   * <span style="color:red;">Important: </span>Empty Optional if no errors were detected.
   */
  Optional<String> getErrorMessage();

  /**
   * @param message Sets an error message for errors which have not been thrown at request time.
   *                Do not set at all (or null) if no error were encountered.
   */
  void setErrorMessage(String message);

  /**
   * @return Licence string - default implementation is returning an empty string.
   * @implSpec Implement according to provider's terms of use!
   */
  default String getLicence() {
    return "";
  }

  /**
   * @param s The licence string.
   * @implSpec Implement according to provider's terms of use!
   */
  void setLicence(String s);

  /**
   * Deeply recycles instance to default values. No new instance is created,
   * the old one is retained.
   *
   * @implSpec Implementations MUST RETAIN the same instance.
   */
  void reset();
}
