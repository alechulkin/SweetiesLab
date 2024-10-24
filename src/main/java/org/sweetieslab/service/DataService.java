package org.sweetieslab.service;

import java.util.List;
import java.util.UUID;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.model.pancakes.PancakeRecipe;

public interface DataService {

  void addOrder(Order order);

  Order getOrder(UUID orderId);

  void addPancakes(PancakeRecipe pancake, UUID orderId, int number);

  void removePancakes(PancakeRecipe pancake, UUID orderId, int number);

  Order removeOrder(UUID orderId);

  boolean orderIsEmpty(UUID orderId);

  int getPancakesCount(UUID orderId);

  List<String> getPancakesDescriptions(UUID orderId);
}
