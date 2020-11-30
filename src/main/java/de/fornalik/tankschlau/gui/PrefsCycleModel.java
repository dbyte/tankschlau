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

package de.fornalik.tankschlau.gui;

import de.fornalik.tankschlau.user.UserPrefs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class PrefsCycleModel {

  private final UserPrefs userPrefs;

  @Autowired
  PrefsCycleModel(UserPrefs userPrefs) {
    this.userPrefs = userPrefs;
  }

  int readUserPrefsPetrolStationsUpdateCycleRate() {
    return userPrefs.readPetrolStationsUpdateCycleRate();
  }

  void writeUserPrefsPetrolStationsUpdateCycleRate(int value) {
    userPrefs.writePetrolStationsUpdateCycleRate(value);
  }

  boolean readUserPrefsPushMessageEnabled() {
    return userPrefs.readPushMessageEnabled();
  }

  void writeUserPrefsPushMessageEnabled(boolean enabled) {
    userPrefs.writePushMessageEnabled(enabled);
  }

  int readUserPrefsPushMessageDelayWithNumberOfCalls() {
    return userPrefs.readPushMessageDelayWithNumberOfCalls();
  }

  void writeUserPrefsPushMessageDelayWithNumberOfCalls(int value) {
    userPrefs.writePushMessageDelayWithNumberOfCalls(value);
  }
}
