package org.sweetieslab.workers;

import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;
import org.sweetieslab.model.order.Address;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.model.pancakes.PancakeFactory;
import org.sweetieslab.service.ManagementService;

public class Disciple implements Runnable {

  private static final Logger LOGGER = Logger.getLogger(Disciple.class.getName());

  private final ManagementService service;
  private final Random random = new Random();
  private static final int NUMBER_OF_ITERATIONS = 10;

  public Disciple(ManagementService service) {
    this.service = service;
  }

  @Override
  public void run() {
    for (int counter = 1; counter <= NUMBER_OF_ITERATIONS; counter++) {
      try {
        Order order = service.createOrder(new Address
            .Builder()
            .building("1")
            .room(String.valueOf(counter))
            .build());
        UUID orderId = order.getId();
        int randomInt = random.nextInt(NUMBER_OF_ITERATIONS);

        switch (randomInt % 5) {
          case 0:
            service.addPancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), orderId, 2);
            break;
          case 1:
            service.addPancakes(PancakeFactory.getDarkChocolatePancake(), orderId, 2);
            break;
          case 2:
            service.addPancakes(PancakeFactory.getDarkChocolateWhippedCreamHazelnutPancake(),
                orderId, 2);
            break;
          case 3:
            service.addPancakes(PancakeFactory.getDarkChocolateWhippedCreamPancake(), orderId, 2);
            break;
          case 4:
            service.addPancakes(PancakeFactory.getRandomPancakeRecipe(), orderId, 2);
            break;
        }
        service.removePancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), orderId, 1);
        if (counter % 3 > 0) {
          service.completeOrder(orderId);
        } else {
          service.cancelOrder(orderId);
        }
      } catch (Exception e) {
        LOGGER.info(e.getMessage());
        break;
      }
    }
  }


}
