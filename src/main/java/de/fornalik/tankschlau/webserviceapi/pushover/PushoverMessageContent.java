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

import de.fornalik.tankschlau.util.Localization;
import de.fornalik.tankschlau.util.StringLegalizer;
import de.fornalik.tankschlau.webserviceapi.common.PetrolStationMessageContent;

// TODO unit tests

/**
 * Concrete implementation of {@link PetrolStationMessageContent} regarding petrol station messages
 * with webservice pushover.net
 */
public class PushoverMessageContent extends PetrolStationMessageContent {
  private String title;
  private String message;

  public PushoverMessageContent(Localization l10n) {
    super(l10n);
  }

  @Override
  public String getTitle() {
    return StringLegalizer.create(title).nullToEmpty().end();
  }

  @Override
  public void setTitle(String s) {
    this.title = StringLegalizer.create(s).safeTrim().end();
  }

  @Override
  public String getMessage() {
    return StringLegalizer.create(message).nullToEmpty().end();
  }

  @Override
  public void setMessage(String text) {
    this.message = StringLegalizer.create(text).safeTrim().end();
  }

  @Override
  public PetrolStationMessageContent newInstance() {
    return new PushoverMessageContent(this.getL10n());
  }
}
