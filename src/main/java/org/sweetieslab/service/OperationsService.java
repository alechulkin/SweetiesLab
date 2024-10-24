package org.sweetieslab.service;

import java.util.Set;
import java.util.UUID;
import org.sweetieslab.model.order.Order;

public interface OperationsService {

  void completeOrder(Order order);

  Order prepareOrder();

  Order deliverOrder();

  Set<UUID> listCompletedOrders();

  Set<UUID> listPreparedOrders();
}
