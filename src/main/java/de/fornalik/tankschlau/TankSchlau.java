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

package de.fornalik.tankschlau;

import de.fornalik.tankschlau.gui.SwingBootstrap;
import de.fornalik.tankschlau.user.ApiKeyManager;
import de.fornalik.tankschlau.user.UserPrefs;
import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@SpringBootApplication
public class TankSchlau {
  private static Logger logger;
  private static ConfigurableApplicationContext context;

  public static void main(String... args) {
    startSpringApplication(args);

    logger = Logger.getLogger(TankSchlau.class.getName());
    logger.log(Level.FINEST, "Spring DI container is active as expected: {0}", context.isActive());

    processVmOptions();
    startSwingApplication();
  }

  private static void startSpringApplication(String... args) {
    SpringApplicationBuilder builder = new SpringApplicationBuilder(TankSchlau.class);
    builder.bannerMode(Banner.Mode.OFF);
    builder.headless(false); // needed for Swing
    context = builder.run(args);
  }

  private static void startSwingApplication() {
    logger.finest("Invoking GUI");
    context.getBean(SwingBootstrap.class).run();
  }

  private static void processVmOptions() {
    // Offers option to pass some data at startup. Ex: -Dmyproperty="My value"

    logger.finest("Processing VM options");

    Optional.ofNullable(System.getProperty("petrolStationsApiKey"))
        .ifPresent(context.getBean("tankerkoenigApikeyManager", ApiKeyManager.class)::write);

    Optional.ofNullable(System.getProperty("geocodingApiKey"))
        .ifPresent(context.getBean("geocodingApikeyManager", ApiKeyManager.class)::write);

    Optional.ofNullable(System.getProperty("pushmessageApiKey"))
        .ifPresent(context.getBean("pushMessageApikeyManager", ApiKeyManager.class)::write);

    Optional.ofNullable(System.getProperty("pushmessageUserId"))
        .ifPresent(context.getBean(UserPrefs.class)::writePushMessageUserId);
  }
}