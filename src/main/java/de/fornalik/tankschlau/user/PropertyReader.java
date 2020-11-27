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

package de.fornalik.tankschlau.user;

/**
 * This interface makes contracts for reading from key-value stores of any kind.
 */
public interface PropertyReader {

  /**
   * Gets the value for a given key.
   *
   * @param key The key for which to get the value.
   * @return The corresponding string value for the given property key.
   */
  String getProperty(String key);
}
