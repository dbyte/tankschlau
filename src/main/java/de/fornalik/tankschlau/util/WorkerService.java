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

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Providing abilities to start a worker (one-shot or cyclic) and forward its results to a
 * consumer when received.
 *
 * @param <ResultType> Type of the result data which is pushed back to the consumer
 *                     right after receiving results from a dedicated worker.
 */
public interface WorkerService<ResultType> {

  /**
   * Execute some worker only once.
   *
   * @param callback A runnable callback. Consumer.accept(...) should be called by the worker when
   *                 it's done with its work and a result exists.
   */
  void startOneShot(Consumer<ResultType> callback);

  /**
   * Execute a worker in timed intervalls.
   *
   * @param callback  A runnable callback. Consumer.accept(...) should be called by the worker when
   *                  it's done with its work and a result exists.
   * @param intervall Intervall between each cycle. TimeUnit must be set by
   *                  {@link #setTimeUnit(TimeUnit)}.
   */
  void startCyclic(Consumer<ResultType> callback, long intervall);

  /**
   * Stop the cycle.
   */
  void stopCyclic();

  /**
   * @return The {@link TimeUnit} for the intervall between cycle.
   */
  TimeUnit getTimeUnit();

  /**
   * Sets the TimeUnit for the intervall between worker cycles.
   *
   * @param timeUnit Sets the TimeUnit. Needed when calling {@link #startCyclic(Consumer, long)}.
   */
  void setTimeUnit(TimeUnit timeUnit);

  /**
   * Should handle the countdown phase until the next cycle of a worker gets invoked.
   *
   * @param callback Pass in a Consumer which handles the service's countdown phase.
   */
  void processCountdown(Consumer<Long> callback);
}
