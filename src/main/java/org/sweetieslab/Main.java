package org.sweetieslab;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Logger;
import org.sweetieslab.model.order.Address;
import org.sweetieslab.model.order.validator.AddressValidator;
import org.sweetieslab.service.CollectionsOperationsService;
import org.sweetieslab.service.ConcurrentMapDataService;
import org.sweetieslab.service.ManagementService;
import org.sweetieslab.workers.Delivery;
import org.sweetieslab.workers.Disciple;
import org.sweetieslab.workers.Sensei;

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
        Future<?> discipleFuture = discipleExecutor.submit(new Disciple(managementService));
        Future<?> senseiFuture = senseiExecutor.submit(new Sensei(managementService));
        Future<?> deliveryFuture = deliveryExecutor.submit(new Delivery(managementService));
        discipleFuture.get();
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