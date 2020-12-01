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

import de.fornalik.tankschlau.util.Localization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.swing.*;
import java.util.concurrent.TimeUnit;

@Controller
public class FooterController {

  private static final Localization L10N = Localization.getInstance();
  private static final String LABEL_NO_NETWORK_ACTIVITY = "label.NoNetworkActivity";

  private final FooterView view;

  @Autowired
  public FooterController(FooterView view) {
    this.view = view;
  }

  void onOneShotWorkerStarted(String name) {
    String legalizedName = name != null ? name : "";
    view.getLabelWork().setText(legalizedName);
    view.getLabelWork().setIcon(view.getIconWork());
  }

  void onOneShotWorkerFinished() {
    view.getLabelWork().setText(L10N.get(LABEL_NO_NETWORK_ACTIVITY));
    view.getLabelWork().setIcon(null);
  }

  void onCyclicWorkerStopped() {
    view.getLabelCountdown().setText(L10N.get("label.AutoUpdateStopped"));
    view.getLabelWork().setText(L10N.get(LABEL_NO_NETWORK_ACTIVITY));
    view.getLabelWork().setIcon(null);
  }

  void updateCountdown(long remaining, TimeUnit timeUnit) {
    final String textForCyclicWorker;
    final String textForSingleWorker;
    final ImageIcon workerIndicator;

    if (remaining <= 0) {
      textForCyclicWorker = L10N.get("label.WaitingForTaskFinish");
      textForSingleWorker = L10N.get("label.TaskRunning", "").trim();
      workerIndicator = view.getIconWork();
    }

    else {
      textForCyclicWorker =
          L10N.get("label.TaskCountdown", remaining + " " + L10N.get("timeUnit." + timeUnit));

      textForSingleWorker = L10N.get(LABEL_NO_NETWORK_ACTIVITY);
      workerIndicator = null;
    }

    view.getLabelCountdown().setText(textForCyclicWorker);
    view.getLabelWork().setText(textForSingleWorker);
    view.getLabelWork().setIcon(workerIndicator);
  }
}
