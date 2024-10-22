package org.sweetieslab.model.order.validator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.sweetieslab.model.order.Address;
import org.sweetieslab.model.order.validator.exception.AddressValidationException;

public interface AddressValidator {

  default List<Consumer<Address>> getRules() {
    return List.of(
        address -> {
          if (isBlank(address.getBuilding())) {
            throw new AddressValidationException("Invalid address: building not set");
          }
        },
        address -> {
          if (isBlank(address.getRoom())) {
            throw new AddressValidationException("Invalid address: room not set");
          }
        },
        address -> {
          if (getValidBuildingsVsRooms().get(address.getBuilding()) == null) {
            throw new AddressValidationException("Invalid address: building not found");
          }
        },
        address -> {
          if (!getValidBuildingsVsRooms().get(address.getBuilding()).contains(address.getRoom())) {
            throw new AddressValidationException("Invalid address: room not found");
          }
        }
    );
  }

  private static boolean isBlank(String building) {
    return building == null || building.isBlank();
  }

  default void validate(Address address) {
    for (Consumer<Address> rule : getRules()) {
      rule.accept(address);
    }
  }

  Map<String, Set<String>> getValidBuildingsVsRooms();
}
