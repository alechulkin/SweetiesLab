package org.sweetieslab.service;

import static java.util.Collections.frequency;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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
import org.sweetieslab.model.order.validator.AddressValidator;
import org.sweetieslab.model.order.validator.exception.AddressValidationException;
import org.sweetieslab.model.order.validator.exception.NotSetAddressValidatorException;
import org.sweetieslab.model.pancakes.PancakeFactory;
import org.sweetieslab.service.exception.OrderNotFoundException;
import org.sweetieslab.service.exception.OrderUpdateException;
import org.sweetieslab.service.exception.PancakesNotFoundForOrder;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManagementServiceTest {

  private final AddressValidator addressValidator =
      new AddressValidator() {
        @Override
        public Map<String, Set<String>> getValidBuildingsVsRooms() {
          return Map.of("10", Set.of("1", "20"));
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
    Address.setValidator(null);

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
  @org.junit.jupiter.api.Order(5)
  public void GivenCompletedQueueIsEmpty_WhenTryingToPrepareOrder_ThenNothingHappensAndAppStopsAfterTimeout_Test() {
    // setup

    // exercise
    Order preparedOrder = service.prepareOrder();

    // verify
    assertNull(preparedOrder);
    assertEquals(0, service.listCompletedOrders().size());
    assertEquals(0, service.listPreparedOrders().size());
  }

  @Test
  @org.junit.jupiter.api.Order(6)
  public void GivenCompletedQueueIsEmpty_WhenTryingToDeliverOrder_ThenNothingHappensAndAppStopsAfterTimeout_Test() {
    // setup

    // exercise
    Order deliveredOrder = service.deliverOrder();

    // verify
    assertNull(deliveredOrder);
    assertEquals(0, service.listCompletedOrders().size());
    assertEquals(0, service.listPreparedOrders().size());
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
  @org.junit.jupiter.api.Order(21)
  public void GivenOrderExists_WhenAddingPancakes_ThenCantAddBecauseOfIncorrectUuid_Test() {
    // setup
    UUID orderId = UUID.randomUUID();

    // exercise
    PancakesNotFoundForOrder expectedException =
        assertThrows(
            PancakesNotFoundForOrder.class,
            () -> {
              service.addPancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), orderId, 1);
            });

    // verify
    assertEquals("Pancakes not found for order: " + orderId, expectedException.getMessage());

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
  public void GivenOrderExists_WhenRemovingIncorrectNumberOfPancakes_ThenCantRemoveAndReportError_Test() {
    // setup

    // exercise

    // verify

    OrderUpdateException expectedException =
        assertThrows(
            OrderUpdateException.class,
            () -> {
              service.removePancakes(PancakeFactory.getDarkChocolatePancakeRecipe(), order.getId(),
                  -2);
            });

    assertEquals("Invalid count for removal: -2", expectedException.getMessage());
    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(31)
  public void GivenOrderNotExists_WhenRemovingPancakes_ThenCantRemoveAndReportError_Test() {
    // setup
    UUID orderId = UUID.randomUUID();

    // exercise
    PancakesNotFoundForOrder expectedException =
        assertThrows(
            PancakesNotFoundForOrder.class,
            () -> {
              service.removePancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), orderId, 1);
            });

    // verify
    assertEquals("Pancakes not found for order: " + orderId, expectedException.getMessage());
    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(32)
  public void GivenPancakesExists_WhenRemovingPancakes_ThenCorrectNumberOfPancakesRemoved_Test() {
    // setup

    // exercise
    service.removePancakes(PancakeFactory.getDarkChocolatePancakeRecipe(), order.getId(), 2);
    service.removePancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), order.getId(), 5);
    service.removePancakes(PancakeFactory.getMilkChocolateHazelnutPancakeRecipe(), order.getId(),
        1);
    service.removePancakes(PancakeFactory.getDarkChocolateWhippedCreamHazelnutPancakeRecipe(),
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
  public void GivenOrderIsEmpty_WhenCompletingOrder_ThenCancelOrder_Test() {
    // setup
    Order emptyOrder = service.createOrder(new Address.Builder().building("10").room("1").build());

    // exercise

    service.completeOrder(emptyOrder);

    // verify

    Set<UUID> completedOrders = service.listCompletedOrders();
    assertFalse(completedOrders.contains(emptyOrder.getId()));

    Set<UUID> preparedOrders = service.listPreparedOrders();
    assertFalse(preparedOrders.contains(emptyOrder.getId()));

    assertThrows(OrderNotFoundException.class, () -> {
      service.getOrder(emptyOrder.getId());
    });

    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(41)
  public void GivenOrderExists_WhenCompletingOrder_ThenOrderCompleted_Test() {
    // setup

    // exercise
    service.completeOrder(order);

    // verify
    Set<UUID> completedOrders = service.listCompletedOrders();
    assertTrue(completedOrders.contains(order.getId()));

    // tear down
  }

  @Test
  @org.junit.jupiter.api.Order(50)
  public void GivenOrderExists_WhenPreparingOrder_ThenOrderPrepared_Test() {
    // setup

    // exercise
    Order prepared = service.prepareOrder();

    // verify

    assertNotNull(prepared);
    assertEquals(order.getId(), prepared.getId());

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

    // exercise
    Order deliveredOrder = service.deliverOrder();

    // verify
    assertNotNull(deliveredOrder);

    Set<UUID> completedOrders = service.listCompletedOrders();
    assertFalse(completedOrders.contains(order.getId()));

    Set<UUID> preparedOrders = service.listPreparedOrders();
    assertFalse(preparedOrders.contains(order.getId()));

    assertThrows(OrderNotFoundException.class, () -> {
      service.getOrder(deliveredOrder.getId());
    });

    assertEquals(order.getId(), deliveredOrder.getId());

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
    service.cancelOrder(order);

    // verify
    Set<UUID> completedOrders = service.listCompletedOrders();
    assertFalse(completedOrders.contains(order.getId()));

    Set<UUID> preparedOrders = service.listPreparedOrders();
    assertFalse(preparedOrders.contains(order.getId()));

    assertThrows(OrderNotFoundException.class, () -> {
      service.getOrder(order.getId());
    });

    // tear down
    order = null;
  }

  private void addPancakes() {
    service.addPancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), order.getId(), 3);
    service.addPancakes(PancakeFactory.getDarkChocolatePancakeRecipe(), order.getId(), 3);
    service.addPancakes(PancakeFactory.getMilkChocolateHazelnutPancakeRecipe(), order.getId(), 3);
  }
}
