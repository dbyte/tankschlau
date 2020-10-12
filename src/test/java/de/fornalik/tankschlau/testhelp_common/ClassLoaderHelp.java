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

package de.fornalik.tankschlau.testhelp_common;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

class ClassLoaderHelp {

  /**
   * @param name     String which represents a resource, separated by "/" as defined
   *                 in {@link ClassLoader#getResource(String)}. Note that the implicit root of the
   *                 final resource url is directory "test/resources".
   * @param forClass The class to get the loader from
   * @return A {@link FileReader} instance, linked to the resource if resource was found, else
   * throws RuntimeException. May also throw RTE if resource was not found.
   */
  static FileReader getFileReaderForResource(String name, Class<?> forClass) {
    Objects.requireNonNull(name);

    ClassLoader loader = forClass.getClassLoader();
    URL fileUrl = loader.getResource(name);

    if (fileUrl == null)
      throw new RuntimeException("Resource " + name + " not found for ClassLoader " + loader.toString());

    try {
      return new FileReader(fileUrl.getFile());
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
