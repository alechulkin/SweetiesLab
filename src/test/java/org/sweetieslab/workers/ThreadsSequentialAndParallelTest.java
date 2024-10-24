package org.sweetieslab.workers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.sweetieslab.workers.Disciple.NUMBER_OF_ITERATIONS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.sweetieslab.model.order.Address;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.model.order.validator.AddressValidator;
import org.sweetieslab.service.CollectionsOperationsService;
import org.sweetieslab.service.ConcurrentMapDataService;
import org.sweetieslab.service.ManagementService;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ThreadsSequentialAndParallelTest {

  private static final Logger LOGGER = Logger.getLogger(
      ThreadsSequentialAndParallelTest.class.getName());

  private ExecutorService tripleExecutorService, singleExecutorService,
      anotherTripleExecutorService;
  private ManagementService managementService = new ManagementService(
      new CollectionsOperationsService(), new ConcurrentMapDataService());

  @BeforeEach
  public void setUp() {
    final Set<String> rooms = new HashSet<>();
    for (int i = 1; i <= 10; i++) {
      rooms.add(String.valueOf(i));
    }
    Address.setValidator(new AddressValidator() {
      @Override
      public Map<String, Set<String>> getValidBuildingsVsRooms() {
        return Map.of("1", rooms, "2", rooms);
      }
    });
    tripleExecutorService = Executors.newFixedThreadPool(3);
    anotherTripleExecutorService = Executors.newFixedThreadPool(3);
    singleExecutorService = Executors.newSingleThreadExecutor();

  }

  @AfterEach
  public void tearDown() {
    singleExecutorService.shutdownNow();
    tripleExecutorService.shutdownNow();
    anotherTripleExecutorService.shutdownNow();
  }

  @Test
  @org.junit.jupiter.api.Order(0)
  public void testDiscipleExecution() throws Exception {
    int numOfForciblyCancelled = 0;
    for (int counter = 1; counter <= NUMBER_OF_ITERATIONS; counter++) {
      Disciple disciple = new Disciple(managementService, counter);
      Future<Boolean> future = tripleExecutorService.submit(disciple);
      boolean forciblyCancelled = future.get();
      if (forciblyCancelled) {
        numOfForciblyCancelled++;
      }
    }
    Set<UUID> completedOrders = managementService.listCompletedOrders();
    assertEquals(
        NUMBER_OF_ITERATIONS - Math.floorDiv(NUMBER_OF_ITERATIONS, 3) - numOfForciblyCancelled,
        completedOrders.size());

    Set<UUID> preparedOrders = managementService.listPreparedOrders();
    assertEquals(0, preparedOrders.size());
  }

  @Test
  @org.junit.jupiter.api.Order(1)
  public void testSenseiExecution() throws Exception {
    int originalCompletedSize = managementService.listCompletedOrders().size();

    Future<?> senseiFuture = singleExecutorService.submit(new AbstractPermanentWorker() {
      @Override
      protected Order doAction() {
        return managementService.prepareOrder();
      }
    });
    senseiFuture.get();

    Set<UUID> completedOrders = managementService.listCompletedOrders();
    assertEquals(0, completedOrders.size());

    Set<UUID> preparedOrders = managementService.listPreparedOrders();
    assertEquals(originalCompletedSize, preparedOrders.size());
  }

  @Test
  @org.junit.jupiter.api.Order(2)
  public void testDeliveryExecution() throws Exception {
    Future<?> deliveryFuture = tripleExecutorService.submit(new AbstractPermanentWorker() {
      @Override
      protected Order doAction() {
        return managementService.deliverOrder();
      }
    });
    deliveryFuture.get();

    Set<UUID> completedOrders = managementService.listCompletedOrders();
    assertEquals(0, completedOrders.size());

    Set<UUID> preparedOrders = managementService.listPreparedOrders();
    assertEquals(0, preparedOrders.size());
  }

  @Test
  @org.junit.jupiter.api.Order(3)
  public void testParallelExecution() throws Exception {
    managementService = new ManagementService(
        new CollectionsOperationsService(), new ConcurrentMapDataService());
    List<Future<Boolean>> futures = new ArrayList<>();
    for (int counter = 1; counter <= NUMBER_OF_ITERATIONS; counter++) {
      Disciple disciple = new Disciple(managementService, counter);
      Future<Boolean> future = tripleExecutorService.submit(disciple);
      futures.add(future);
    }

    Future<?> senseiFuture = singleExecutorService.submit(new AbstractPermanentWorker() {
      @Override
      protected Order doAction() {
        return managementService.prepareOrder();
      }
    });

    Future<?> deliveryFuture = anotherTripleExecutorService.submit(new AbstractPermanentWorker() {
      @Override
      protected Order doAction() {
        return managementService.deliverOrder();
      }
    });

    futures.forEach(future -> {
      try {
        future.get();
      } catch (Exception e) {
        Thread.currentThread().interrupt();
        LOGGER.info(e.getMessage());
      }
    });
    senseiFuture.get();
    deliveryFuture.get();

    Set<UUID> completedOrders = managementService.listCompletedOrders();
    assertEquals(0, completedOrders.size());

    Set<UUID> preparedOrders = managementService.listPreparedOrders();
    assertEquals(0, preparedOrders.size());
  }
}