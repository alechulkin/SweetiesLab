package org.sweetieslab.workers;

import org.sweetieslab.model.order.Order;

public abstract class AbstractPermanentWorker implements Runnable {

  protected abstract Order doAction();

  @Override
  public void run() {
    while (true) {
      doAction();
    }
  }
}