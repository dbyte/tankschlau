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

package de.fornalik.tankschlau.webserviceapi.common;

/**
 * Describes the content of a message
 */
public interface MessageContent {

  /**
   * @return Title of the message.
   */
  String getTitle();

  /**
   * @param s Title of the message.
   */
  void setTitle(String s);

  /**
   * @return Main message text.
   */
  String getMessage();

  /**
   * @param text Main message text.
   */
  void setMessage(String text);

  /**
   * Use right before setting up new content from the client. As of IoC, the implementing classes
   * might cover the app's lifecycle, so here's a way to cleanup without destroying the graph
   * of the concrete instance.
   *
   * @return A new instance of the implementing class or its superclass.
   */
  @SuppressWarnings("UnusedReturnValue")
  MessageContent newInstance();
}
