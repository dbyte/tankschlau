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

package de.fornalik.tankschlau.util;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Customized apache.commons.lang3 {@link ToStringBuilder} used by this application.
 *
 * @see ToStringBuilder
 */
public class MyToStringBuilder extends ToStringBuilder {

  /**
   * @see ToStringBuilder
   */
  public MyToStringBuilder(Object object) {
    super(object, MyToStringStyle.getInstance());
  }
}

/**
 * Thread-safe-lazy-loading Singleton which provides customized settings for the
 * apache.commons.lang3 {@link ToStringBuilder}.
 */
class MyToStringStyle extends ToStringStyle {
  private static MyToStringStyle instance;

  private MyToStringStyle() {
    super();
  }

  /**
   * @return Thread-safe-lazy-loading singleton of MyToStringStyle.
   */
  static synchronized MyToStringStyle getInstance() {
    if (instance == null)
      instance = new MyToStringStyle();

    // Hide package paths
    instance.setUseShortClassName(true);

    return instance;
  }
}