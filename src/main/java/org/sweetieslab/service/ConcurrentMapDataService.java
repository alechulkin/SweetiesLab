package org.sweetieslab.service;

import static java.util.Collections.nCopies;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.model.pancakes.PancakeRecipe;
import org.sweetieslab.service.exception.OrderNotFoundException;
import org.sweetieslab.service.exception.OrderUpdateException;
import org.sweetieslab.service.exception.PancakesNotFoundForOrder;

public class ConcurrentMapDataService implements DataService {

  private static final Logger LOGGER = Logger.getLogger(ConcurrentMapDataService.class.getName());

  private ConcurrentMap<UUID, Order> orders = new ConcurrentHashMap<>();
  private ConcurrentMap<UUID, ConcurrentMap<PancakeRecipe, Integer>> ordersVsPancakes =
      new ConcurrentHashMap<>();

  @Override
  public void addOrder(Order order) {
    UUID orderId = order.getId();
    orders.put(orderId, order);
    ordersVsPancakes.put(orderId, new ConcurrentHashMap<>());
  }

  @Override
  public Order getOrder(UUID orderId) {
    Order order = orders.get(orderId);
    if (order == null) {
      throw new OrderNotFoundException("Order not found: " + orderId);
    }
    return order;
  }

  @Override
  public void addPancakes(PancakeRecipe pancake, UUID orderId, int count) {
    if (count < 1) {
      throw new OrderUpdateException("Invalid count for adding: " + count);
    }
    ConcurrentMap<PancakeRecipe, Integer> pancakes = ordersVsPancakes.get(orderId);
    if (pancakes == null) {
      throw new PancakesNotFoundForOrder("Pancakes not found for order: " + orderId);
    }
    pancakes.merge(pancake, count, Integer::sum);
    ordersVsPancakes.putIfAbsent(orderId, pancakes);
  }

  @Override
  public void removePancakes(PancakeRecipe pancake, UUID orderId, int count) {
    if (count < 1) {
      throw new OrderUpdateException("Invalid count for removal: " + count);
    }
    ConcurrentMap<PancakeRecipe, Integer> pancakes = getOrderPancakes(orderId);
    Integer foundNumber = pancakes.get(pancake);
    if (foundNumber == null) {
      LOGGER.warning("Pancake not found for removal: " + pancake);
    } else {
      pancakes.compute(pancake, (k, v) -> {
        int newNumber = v - count;
        return newNumber > 0 ? newNumber : null;
      });
    }
  }

  private ConcurrentMap<PancakeRecipe, Integer> getOrderPancakes(UUID orderId) {
    ConcurrentMap<PancakeRecipe, Integer> pancakes = ordersVsPancakes.get(orderId);
    if (pancakes == null) {
      throw new PancakesNotFoundForOrder("Pancakes not found for order: " + orderId);
    }
    return pancakes;
  }

  @Override
  public boolean orderIsEmpty(UUID orderId) {
    return getOrderPancakes(orderId).isEmpty();
  }

  @Override
  public int getPancakesCount(UUID orderId) {
    return getOrderPancakes(orderId).values().stream().mapToInt(Integer::intValue).sum();
  }

  @Override
  public List<String> getPancakesDescriptions(UUID orderId) {
    return getOrderPancakes(orderId).entrySet()
        .stream()
        .flatMap(entry -> nCopies(entry.getValue(), entry.getKey().toString()).stream())
        .toList();
  }

  @Override
  public Order removeOrder(UUID orderId) {
    ordersVsPancakes.remove(orderId);
    return orders.remove(orderId);
  }
}
