package org.sweetieslab.service;

import org.sweetieslab.model.order.Order;

public interface OperationsService {

  void completeOrder(Order order);

  Order prepareOrder();

  Order deliverOrder();
}
