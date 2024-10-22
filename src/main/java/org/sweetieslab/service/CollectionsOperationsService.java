package org.sweetieslab.service;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.service.exception.OperationsServiceException;

public class CollectionsOperationsService implements OperationsService {

  private final BlockingDeque<Order> completed = new LinkedBlockingDeque<>();
  private final BlockingDeque<Order> prepared = new LinkedBlockingDeque<>();

  @Override
  public void completeOrder(Order order) {
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new OperationsServiceException("Interrupted while completing order");
    }
    try {
      completed.put(order);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new OperationsServiceException("Interrupted while adding order to completed queue");
    }
  }

  @Override
  public Order prepareOrder() {
    Order order;
    try {
      order = completed.take();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new OperationsServiceException("Interrupted while taking order from completed queue");
    }
    try {
      Thread.sleep(500);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new OperationsServiceException("Interrupted while preparing order");
    }
    try {
      prepared.put(order);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new OperationsServiceException("Interrupted while adding order to prepared queue");
    }
    return order;
  }

  @Override
  public Order deliverOrder() {
    Order order;
    try {
      order = prepared.take();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new OperationsServiceException("Interrupted while taking order from prepared queue");
    }
    return order;
  }
}
