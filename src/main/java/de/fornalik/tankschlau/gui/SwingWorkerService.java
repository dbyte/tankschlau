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

import de.fornalik.tankschlau.util.RunnableCallbackWorker;
import de.fornalik.tankschlau.util.WorkerService;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Provides abilities to start a worker on a new Thread (one-shot or cyclic) and forward its
 * results to a consumer when received.
 *
 * @param <ResultType> Type of the result data which is pushed back to the consumer
 *                     right after receiving results from RunnableCallbackWorker.
 */
class SwingWorkerService<ResultType> implements WorkerService<ResultType> {
  private static final Logger LOGGER = Logger.getLogger(SwingWorkerService.class.getName());
  private static final int INITIAL_DELAY_SECONDS = 3;

  private final RunnableCallbackWorker<ResultType> worker;
  private final ScheduledExecutorService workerSchedule;

  private ScheduledFuture<?> workerFuture;
  private ScheduledExecutorService countdownAgent;
  private TimeUnit timeUnit;

  SwingWorkerService(RunnableCallbackWorker<ResultType> worker) {
    this.worker = worker;
    this.workerSchedule = Executors.newSingleThreadScheduledExecutor();
    this.workerFuture = null;
    this.countdownAgent = Executors.newSingleThreadScheduledExecutor();
    this.timeUnit = TimeUnit.SECONDS; // default
  }

  @Override
  public TimeUnit getTimeUnit() {
    return timeUnit;
  }

  @Override
  public void setTimeUnit(TimeUnit timeUnit) {
    this.timeUnit = Objects.requireNonNull(timeUnit);
  }

  @Override
  public void startOneShot(Consumer<ResultType> callback) {
    worker.setCallback(callback);
    ExecutorService executorService = Executors.newSingleThreadExecutor();
    executorService.submit(worker);
    LOGGER.info("One Shot Worker started.");
  }

  @Override
  public void startCyclic(Consumer<ResultType> callback, long intervall) {
    if (workerFuture != null && !workerFuture.isDone())
      return;

    worker.setCallback(callback);
    workerFuture = workerSchedule.scheduleAtFixedRate(
        worker, INITIAL_DELAY_SECONDS, intervall, timeUnit);

    LOGGER.info("Cyclic PetrolStationWorker started.");
  }

  @Override
  public void stopCyclic() {
    if (workerFuture == null) {
      LOGGER.warning("Cyclic PetrolStationWorker has no future that can be stopped.");
      return;
    }

    workerFuture.cancel(false);
    countdownAgent.shutdown();

    LOGGER.info("Cyclic PetrolStationWorker stopped (running task continues until finished).");
  }

  @Override
  public void processCountdown(Consumer<Long> callback) {
    if (countdownAgent.isShutdown())
      countdownAgent = Executors.newSingleThreadScheduledExecutor();

    countdownAgent.scheduleAtFixedRate(
        () -> callback.accept(workerFuture.getDelay(timeUnit)),
        0,
        1,
        timeUnit);
  }
}
