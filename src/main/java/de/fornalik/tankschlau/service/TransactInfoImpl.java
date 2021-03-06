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

package de.fornalik.tankschlau.service;

import de.fornalik.tankschlau.util.MyToStringBuilder;
import de.fornalik.tankschlau.util.StringLegalizer;

import java.util.Optional;

public class TransactInfoImpl implements TransactInfo {
  private String status;
  private String errorMessage;
  private String licence;

  public TransactInfoImpl() {
    this.reset();
  }

  @Override
  public String getStatus() {
    return nullToEmpty(status);
  }

  @Override
  public void setStatus(String s) {
    this.status = s;
  }

  @Override
  public Optional<String> getErrorMessage() {
    return Optional.ofNullable(errorMessage);
  }

  @Override
  public void setErrorMessage(String s) {
    this.errorMessage = StringLegalizer.create(s).safeTrim().end();
  }

  /**
   * @return Licence string default: Empty string. Override as needed.
   * @implSpec Override according to provider's terms of use!
   */
  @Override
  public String getLicence() {
    return nullToEmpty(licence);
  }

  @Override
  public void setLicence(String s) {
    this.licence = s;
  }

  @Override
  public void reset() {
    this.licence = "";
    this.errorMessage = null;
    this.status = "";
  }

  private String nullToEmpty(String s) {
    return s != null ? s : "";
  }

  @Override
  public String toString() {
    return new MyToStringBuilder(this)
        .append("status", status)
        .append("message", errorMessage)
        .append("licence", licence)
        .toString();
  }
}

