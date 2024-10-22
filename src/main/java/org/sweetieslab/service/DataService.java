package org.sweetieslab.service;

import java.util.Set;
import java.util.UUID;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.model.pancakes.PancakeRecipe;

public interface DataService {

  void addOrder(Order order);

  Order getOrder(UUID orderId);

  Order addPancakes(PancakeRecipe pancake, UUID orderId, int number);

  Order removePancakes(PancakeRecipe pancake, UUID orderId, int number);

  Order removeOrder(UUID orderId);

  Set<UUID> listPreparedOrders();

  Set<UUID> listCompletedOrders();

  Order completeOrder(UUID orderId);

  void prepareOrder(UUID orderId);
}
