package org.sweetieslab.service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.model.order.OrderStatus;
import org.sweetieslab.model.pancakes.PancakeRecipe;
import org.sweetieslab.service.exception.OrderNotFoundException;

public class ConcurrentMapDataService implements DataService {

  private ConcurrentMap<UUID, Order> orders = new ConcurrentHashMap<>();

  @Override
  public void addOrder(Order order) {
    orders.put(order.getId(), order);
  }

  @Override
  public Order getOrder(UUID orderId) {
    return orders.get(orderId);
  }

  @Override
  public Order addPancakes(PancakeRecipe pancake, UUID orderId, int number) {
    Order order = getOrderOrThrowError(orderId);
    order.addPancakes(pancake, number);
    return order;
  }

  @Override
  public Order removePancakes(PancakeRecipe pancake, UUID orderId, int number) {
    Order order = getOrderOrThrowError(orderId);
    order.removePancakes(pancake, number);
    return order;
  }

  @Override
  public Order removeOrder(UUID orderId) {
    return orders.remove(orderId);
  }

  @Override
  public Order completeOrder(UUID orderId) {
    Order order = getOrderOrThrowError(orderId);
    order.complete();
    return order;
  }

  @Override
  public void prepareOrder(UUID orderId) {
    Order order = getOrderOrThrowError(orderId);
    order.prepare();
  }

  @Override
  public Set<UUID> listCompletedOrders() {
    return listFilteredOrders(OrderStatus.COMPLETED);
  }

  @Override
  public Set<UUID> listPreparedOrders() {
    return listFilteredOrders(OrderStatus.PREPARED);
  }

  private Order getOrderOrThrowError(UUID orderId) {
    Order order = getOrder(orderId);
    if (order == null) {
      throw new OrderNotFoundException("Order not found: " + orderId);
    }
    return order;
  }

  private Set<UUID> listFilteredOrders(OrderStatus status) {
    return orders.values().stream()
        .filter(order -> order.getStatus() == status)
        .map(Order::getId)
        .collect(Collectors.toSet());
  }
}
