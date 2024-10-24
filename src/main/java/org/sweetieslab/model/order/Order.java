package org.sweetieslab.model.order;

import java.util.Objects;
import java.util.UUID;

public class Order {

  private final UUID id;
  private final String building;
  private final String room;

  public Order(Address address) {
    this.id = UUID.randomUUID();
    this.building = address.getBuilding();
    this.room = address.getRoom();
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
