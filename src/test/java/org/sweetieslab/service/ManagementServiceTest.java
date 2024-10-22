package org.sweetieslab.service;

import static java.util.Collections.frequency;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.sweetieslab.model.order.Address;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.model.order.exception.OrderUpdateException;
import org.sweetieslab.model.order.validator.AddressValidator;
import org.sweetieslab.model.order.validator.exception.AddressValidationException;
import org.sweetieslab.model.order.validator.exception.NotSetAddressValidatorException;
import org.sweetieslab.model.pancakes.PancakeFactory;
import org.sweetieslab.service.exception.OrderNotFoundException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManagementServiceTest {

  private final AddressValidator addressValidator =
      new AddressValidator() {
        @Override
        public Map<String, Set<String>> getValidBuildingsVsRooms() {
          return Map.of("10", Set.of("20"));
        }
      };
  private final ManagementService service = new ManagementService(
      new CollectionsOperationsService(), new ConcurrentMapDataService());
  private Order order = null;

  private final static String MILK_CHOCOLATE_PANCAKE_DESCRIPTION = "Delicious pancake with milk chocolate (50), flour (100), egg (1), milk (200)!";
  private final static String DARK_CHOCOLATE_PANCAKE_DESCRIPTION = "Delicious pancake with dark chocolate (50), flour (100), egg (1), milk (150)!";
  private final static String MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION = "Delicious pancake with milk chocolate (50), flour (100), egg (1), milk (200), hazelnut (50)!";

  @Test
  @org.junit.jupiter.api.Order(0)
  public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderNotCreatedWithNoValidatorSet_Test() {
    // setup

    // exercise
    assertThrows(
        NotSetAddressValidatorException.class,
        () -> {
          service.createOrder(new Address.Builder().building("").room("20").build());
        });

  }

  @Test
  @org.junit.jupiter.api.Order(1)
  public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderNotCreatedWithBlankBuilding_Test() {
    // setup
    Address.setValidator(addressValidator);

    // exercise
    AddressValidationException expectedException =
        assertThrows(
            AddressValidationException.class,
            () -> {
              service.createOrder(new Address.Builder().building("").room("20").build());
            });

    assertEquals("Invalid address: building not set", expectedException.getMessage());
  }

  @Test
  @org.junit.jupiter.api.Order(2)
  public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderNotCreatedWithBlankRoom_Test() {
    // setup

    // exercise
    AddressValidationException expectedException =
        assertThrows(
            AddressValidationException.class,
            () -> {
              service.createOrder(new Address.Builder().building("10").build());
            });

    assertEquals("Invalid address: room not set", expectedException.getMessage());
  }

  @Test
  @org.junit.jupiter.api.Order(3)
  public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderNotCreatedWithIncorrectBuilding_Test() {
    // setup

    // exercise
    AddressValidationException expectedException =
        assertThrows(
            AddressValidationException.class,
            () -> {
              service.createOrder(new Address.Builder().building("1").room("20").build());
            });

    assertEquals("Invalid address: building not found", expectedException.getMessage());
  }

  @Test
  @org.junit.jupiter.api.Order(4)
  public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderNotCreatedWithIncorrectRoom_Test() {
    // setup

    // exercise
    AddressValidationException expectedException =
        assertThrows(
            AddressValidationException.class,
            () -> {
              service.createOrder(new Address.Builder().building("10").room("11").build());
            });

    assertEquals("Invalid address: room not found", expectedException.getMessage());
  }

  @Test
  @org.junit.jupiter.api.Order(10)
  public void GivenOrderDoesNotExist_WhenCreatingOrder_ThenOrderCreatedWithCorrectData_Test() {
    // setup

    // exercise
    order = service.createOrder(new Address.Builder().building("10").room("20").build());

    assertEquals("10", order.getBuilding());
    assertEquals("20", order.getRoom());

    // verify

    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(20)
  public void GivenOrderExists_WhenAddingPancakes_ThenCantAddBecauseOfIncorrectUuid_Test() {
    // setup
    UUID orderId = UUID.randomUUID();

    // exercise
    OrderNotFoundException expectedException =
        assertThrows(
            OrderNotFoundException.class,
            () -> {
              service.addPancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), orderId, 0);
            });

    // verify
    assertEquals("Order not found: " + orderId, expectedException.getMessage());

    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(21)
  public void GivenOrderExists_WhenAddingPancakes_ThenCantAddBecauseOfIncorrectCount_Test() {
    // setup

    // exercise
    OrderUpdateException expectedException =
        assertThrows(
            OrderUpdateException.class,
            () -> {
              service.addPancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), order.getId(), 0);
            });

    // verify
    assertEquals("Invalid count for adding: 0", expectedException.getMessage());

    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(22)
  public void GivenOrderExists_WhenAddingPancakes_ThenCorrectNumberOfPancakesAdded_Test() {
    // setup

    // exercise
    addPancakes();

    // verify
    List<String> ordersPancakes = service.viewOrder(order.getId());

    assertEquals(3, frequency(ordersPancakes, DARK_CHOCOLATE_PANCAKE_DESCRIPTION));
    assertEquals(3, frequency(ordersPancakes, MILK_CHOCOLATE_PANCAKE_DESCRIPTION));
    assertEquals(3, frequency(ordersPancakes, MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION));
    assertEquals(9, ordersPancakes.size());

    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(30)
  public void GivenOrderNotExists_WhenRemovingPancakes_ThenCantRemoveAndReportError_Test() {
    // setup
    UUID orderId = UUID.randomUUID();

    // exercise
    OrderNotFoundException expectedException =
        assertThrows(
            OrderNotFoundException.class,
            () -> {
              service.removePancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), orderId, 0);
            });

    // verify
    assertEquals("Order not found: " + orderId, expectedException.getMessage());
    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(31)
  public void GivenOrderExists_WhenRemovingIncorrectNumberOfPancakes_ThenCantRemoveAndReportError_Test() {
    // setup

    // exercise

    // verify

    OrderUpdateException expectedException =
        assertThrows(
            OrderUpdateException.class,
            () -> {
              service.removePancakes(PancakeFactory.getDarkChocolatePancake(), order.getId(), -2);
            });

    assertEquals("Invalid count for removal: -2", expectedException.getMessage());
    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(32)
  public void GivenPancakesExists_WhenRemovingPancakes_ThenCorrectNumberOfPancakesRemoved_Test() {
    // setup

    // exercise
    service.removePancakes(PancakeFactory.getDarkChocolatePancake(), order.getId(), 2);
    service.removePancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), order.getId(), 5);
    service.removePancakes(PancakeFactory.getMilkChocolateHazelnutPancakeRecipe(), order.getId(),
        1);
    service.removePancakes(PancakeFactory.getDarkChocolateWhippedCreamHazelnutPancake(),
        order.getId(), 1);

    // verify
    List<String> ordersPancakes = service.viewOrder(order.getId());

    assertEquals(1, frequency(ordersPancakes, DARK_CHOCOLATE_PANCAKE_DESCRIPTION));
    assertEquals(2, frequency(ordersPancakes, MILK_CHOCOLATE_HAZELNUTS_PANCAKE_DESCRIPTION));
    assertEquals(3, ordersPancakes.size());

    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(40)
  public void GivenOrderNotExists_WhenCompletingOrder_ThenThrowError_Test() {
    // setup
    UUID orderId = UUID.randomUUID();

    // exercise

    OrderNotFoundException expectedException =
        assertThrows(
            OrderNotFoundException.class,
            () -> {
              service.completeOrder(orderId);
            });

    // verify

    assertEquals("Order not found: " + orderId, expectedException.getMessage());

    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(41)
  public void GivenOrderExists_WhenCompletingOrder_ThenOrderCompleted_Test() {
    // setup

    // exercise
    service.completeOrder(order.getId());

    // verify
    Set<UUID> completedOrdersOrders = service.listCompletedOrders();
    assertTrue(completedOrdersOrders.contains(order.getId()));

    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(50)
  public void GivenOrderExists_WhenPreparingOrder_ThenOrderPrepared_Test() {
    // setup

    // exercise
    service.prepareOrder();

    // verify
    Set<UUID> completedOrders = service.listCompletedOrders();
    assertFalse(completedOrders.contains(order.getId()));

    Set<UUID> preparedOrders = service.listPreparedOrders();
    assertTrue(preparedOrders.contains(order.getId()));

    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(60)
  public void GivenOrderExists_WhenDeliveringOrder_ThenCorrectOrderReturnedAndOrderRemovedFromTheDatabase_Test() {
    // setup
    List<String> pancakesToDeliver = service.viewOrder(order.getId());

    // exercise
    Order deliveredOrder = service.deliverOrder();

    // verify
    Set<UUID> completedOrders = service.listCompletedOrders();
    assertFalse(completedOrders.contains(order.getId()));

    Set<UUID> preparedOrders = service.listPreparedOrders();
    assertFalse(preparedOrders.contains(order.getId()));

    Order foundOrder = service.getOrder(deliveredOrder.getId());

    assertNull(foundOrder);
    assertEquals(order.getId(), deliveredOrder.getId());
    assertEquals(pancakesToDeliver, deliveredOrder.getPancakesDescriptions());

    // tear down
    order = null;
  }

  @Test
  @org.junit.jupiter.api.Order(70)
  public void GivenOrderExists_WhenCancellingOrder_ThenOrderAndPancakesRemoved_Test() {
    // setup
    order = service.createOrder(new Address.Builder().building("10").room("20").build());
    addPancakes();

    // exercise
    service.cancelOrder(order.getId());

    // verify
    Set<UUID> completedOrders = service.listCompletedOrders();
    assertFalse(completedOrders.contains(order.getId()));

    Set<UUID> preparedOrders = service.listPreparedOrders();
    assertFalse(preparedOrders.contains(order.getId()));

    Order foundOrder = service.getOrder(order.getId());
    assertNull(foundOrder);

    // tear down
    order = null;
  }

  private void addPancakes() {
    service.addPancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), order.getId(), 3);
    service.addPancakes(PancakeFactory.getDarkChocolatePancake(), order.getId(), 3);
    service.addPancakes(PancakeFactory.getMilkChocolateHazelnutPancakeRecipe(), order.getId(), 3);
  }
}
