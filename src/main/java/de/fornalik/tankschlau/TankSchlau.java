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

import de.fornalik.tankschlau.gui.MainWindow;
import de.fornalik.tankschlau.user.ApiKeyVmOptionHandler;
import de.fornalik.tankschlau.util.LoggingConfig;
import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import javax.swing.*;

@SpringBootApplication
public class TankSchlau {

  public static void main(String... args) {
    ConfigurableApplicationContext context = createApplicationContext(args);

    LoggingConfig.init();
    processVmParameters(context);
    startSwingApplication(context);
  }

  private static ConfigurableApplicationContext createApplicationContext(String... args) {
    return new SpringApplicationBuilder(TankSchlau.class)
        .bannerMode(Banner.Mode.OFF)
        .headless(false) // needed for Swing
        .web(WebApplicationType.NONE)
        .run(args);
  }

  private static void processVmParameters(ConfigurableApplicationContext context) {
    context.getBean(ApiKeyVmOptionHandler.class).processVmOptions();
  }

  private static void startSwingApplication(ConfigurableApplicationContext context) {
    SwingUtilities.invokeLater(() -> {
      MainWindow mainWindow = context.getBean(MainWindow.class);
      mainWindow.initView();
    });
  }
}