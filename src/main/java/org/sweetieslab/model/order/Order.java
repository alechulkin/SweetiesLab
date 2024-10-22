package org.sweetieslab.model.order;

import static java.util.Collections.nCopies;
import static org.sweetieslab.model.order.OrderStatus.COMPLETED;
import static org.sweetieslab.model.order.OrderStatus.NEW;
import static org.sweetieslab.model.order.OrderStatus.PREPARED;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import org.sweetieslab.model.order.exception.OrderUpdateException;
import org.sweetieslab.model.pancakes.PancakeRecipe;

public class Order {

  private final UUID id;
  private final String building;
  private final String room;
  private final Map<PancakeRecipe, Integer> pancakes = new HashMap<>();
  private OrderStatus status;

  public Order(Address address) {
    this.id = UUID.randomUUID();
    this.building = address.getBuilding();
    this.room = address.getRoom();
    this.status = NEW;
  }

  public OrderStatus getStatus() {
    return status;
  }

  public void complete() {
    this.status = COMPLETED;
  }

  public void prepare() {
    this.status = PREPARED;
  }

  public int getPancakesCount() {
    return pancakes.size();
  }

  public List<String> getPancakesDescriptions() {
    return pancakes.entrySet()
        .stream()
        .flatMap(entry -> nCopies(entry.getValue(),
            entry.getKey().toString()).stream())
        .toList();
  }

  public void addPancakes(PancakeRecipe pancake, int count) {
    if (count < 1) {
      throw new OrderUpdateException("Invalid count for adding: " + count);
    }
    pancakes.put(pancake, pancakes.getOrDefault(pancake, 0) + count);
  }

  public void removePancakes(PancakeRecipe pancake, int count) {
    if (count < 1) {
      throw new OrderUpdateException("Invalid count for removal: " + count);
    }
    int existing = pancakes.getOrDefault(pancake, 0);
    if (existing <= count) {
      pancakes.remove(pancake);
    } else {
      pancakes.put(pancake, existing - count);
    }
  }

  public UUID getId() {
    return id;
  }

  public String getBuilding() {
    return building;
  }

  public String getRoom() {
    return room;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Order order)) {
      return false;
    }
    return Objects.equals(id, order.id);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(id);
  }
}
