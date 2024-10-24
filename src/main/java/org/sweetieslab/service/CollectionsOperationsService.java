package org.sweetieslab.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.service.exception.OperationsServiceException;

public class CollectionsOperationsService implements OperationsService {

  public static final long TIMEOUT = 15L;
  private final BlockingQueue<Order> completed = new LinkedBlockingQueue<>();
  private final BlockingQueue<Order> prepared = new LinkedBlockingQueue<>();

  @Override
  public void completeOrder(Order order) {
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
      order = completed.poll(TIMEOUT, TimeUnit.SECONDS);
      if (order == null) {
        return null;
      }
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
      order = prepared.poll(TIMEOUT, TimeUnit.SECONDS);
      if (order == null) {
        return null;
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new OperationsServiceException("Interrupted while taking order from prepared queue");
    }
    return order;
  }

  @Override
  public Set<UUID> listCompletedOrders() {
    return listOrders(completed);
  }

  @Override
  public Set<UUID> listPreparedOrders() {
    return listOrders(prepared);
  }

  private Set<UUID> listOrders(BlockingQueue<Order> queue) {
    return queue.stream().map(Order::getId).collect(Collectors.toSet());
  }
}
