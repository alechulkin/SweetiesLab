package org.sweetieslab.workers;

import org.sweetieslab.service.ManagementService;

public class Delivery implements Runnable {

  private final ManagementService service;

  public Delivery(ManagementService service) {
    this.service = service;
  }

  @Override
  public void run() {
    while (true) {
      service.deliverOrder();
    }
  }
}
