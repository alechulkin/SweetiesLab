package org.sweetieslab.workers;

import org.sweetieslab.service.ManagementService;

public class Sensei implements Runnable {

  private final ManagementService service;

  public Sensei(ManagementService service) {
    this.service = service;
  }

  @Override
  public void run() {
    while (true) {
      service.prepareOrder();
    }
  }
}
