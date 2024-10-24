package org.sweetieslab;

import static org.sweetieslab.workers.Disciple.NUMBER_OF_ITERATIONS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import org.sweetieslab.model.order.Address;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.model.order.validator.AddressValidator;
import org.sweetieslab.service.CollectionsOperationsService;
import org.sweetieslab.service.ConcurrentMapDataService;
import org.sweetieslab.service.ManagementService;
import org.sweetieslab.workers.AbstractPermanentWorker;
import org.sweetieslab.workers.Disciple;

public class Main {

  private static final Logger LOGGER = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) {
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
      ExecutorService discipleExecutor = Executors.newFixedThreadPool(3);
      ExecutorService senseiExecutor = Executors.newSingleThreadExecutor();
      ExecutorService deliveryExecutor = Executors.newFixedThreadPool(3);
      ManagementService managementService = new ManagementService(
          new CollectionsOperationsService(), new ConcurrentMapDataService());
      try {
        List<Future<Boolean>> discipleFutures = new ArrayList<>();
        for (int counter = 1; counter <= NUMBER_OF_ITERATIONS; counter++) {
          discipleFutures.add(discipleExecutor.submit(new Disciple(managementService,
              counter)));
        }
        Future<?> senseiFuture = senseiExecutor.submit(new AbstractPermanentWorker() {
          @Override
          protected Order doAction() {
            return managementService.prepareOrder();
          }
        });
        Future<?> deliveryFuture = deliveryExecutor.submit(new AbstractPermanentWorker() {
          @Override
          protected Order doAction() {
            return managementService.deliverOrder();
          }
        });
        discipleFutures.forEach(df -> {
          try {
            df.get();
          } catch (Exception e) {
            LOGGER.info(e.getMessage());
            Thread.currentThread().interrupt();
          }
        });
        senseiFuture.get();
        deliveryFuture.get();
      } catch (Exception e) {
        LOGGER.info(e.getMessage());
        Thread.currentThread().interrupt();
      } finally {
        discipleExecutor.shutdownNow();
        senseiExecutor.shutdownNow();
        deliveryExecutor.shutdownNow();
      }
    }
}