package org.sweetieslab.workers;

import org.sweetieslab.model.order.Order;

public abstract class AbstractPermanentWorker implements Runnable {

  private static final long DURATION_IN_NANOS = 20_000_000_000L;

  protected abstract Order doAction();

  @Override
  public void run() {
    long startTime = System.nanoTime();
    while (timeNotElapsed(startTime)) {
      doAction();
    }
  }

  private static boolean timeNotElapsed(long startTime) {
    return System.nanoTime() - startTime < DURATION_IN_NANOS;
  }
}