package org.sweetieslab.model.order.validator;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sweetieslab.model.order.Address;
import org.sweetieslab.model.order.validator.exception.AddressValidationException;

class AddressValidatorTest {

  private AddressValidator addressValidator;

  @BeforeEach
  void setUp() {
    addressValidator = new AddressValidator() {
      @Override
      public Map<String, Set<String>> getValidBuildingsVsRooms() {
        return Map.of(
            "BuildingA", Set.of("Room1", "Room2"),
            "BuildingB", Set.of("Room3", "Room4")
        );
      }
    };
    Address.setValidator(addressValidator);
  }

  @Test
  void testBlankBuilding() {
    AddressValidationException exception = assertThrows(AddressValidationException.class,
        () -> new Address.Builder().building("").room("Room1").build());
    assertEquals("Invalid address: building not set", exception.getMessage());
  }

  @Test
  void testBlankRoom() {
    AddressValidationException exception = assertThrows(AddressValidationException.class,
        () -> new Address.Builder().building("BuildingA").room("").build());
    assertEquals("Invalid address: room not set", exception.getMessage());
  }

  @Test
  void testInvalidBuilding() {
    AddressValidationException exception = assertThrows(AddressValidationException.class,
        () -> new Address.Builder().building("InvalidBuilding").room("Room1").build());
    assertEquals("Invalid address: building not found", exception.getMessage());
  }

  @Test
  void testInvalidRoom() {
    AddressValidationException exception = assertThrows(AddressValidationException.class,
        () -> new Address.Builder().building("BuildingA").room("InvalidRoom").build());
    assertEquals("Invalid address: room not found", exception.getMessage());
  }

  @Test
  void testValidAddress() {
    Address address = new Address.Builder().building("BuildingA").room("Room1").build();
    assertDoesNotThrow(() -> addressValidator.validate(address));
  }
}