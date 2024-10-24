package org.sweetieslab.workers;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Logger;
import org.sweetieslab.model.order.Address;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.model.pancakes.PancakeFactory;
import org.sweetieslab.service.ManagementService;

public class Disciple implements Callable<Boolean> {

  public static final int NUMBER_OF_ITERATIONS = 10;

  private static final Logger LOGGER = Logger.getLogger(Disciple.class.getName());

  private final ManagementService service;
  private final int counter;
  private final Random random = new Random();

  public Disciple(ManagementService service, int counter) {
    this.service = service;
    this.counter = counter;
  }

  @Override
  public Boolean call() {
    boolean forciblyCancelled = false;
    try {
      Order order = service.createOrder(new Address
          .Builder()
          .building("1")
          .room(String.valueOf(counter))
          .build());
      UUID orderId = order.getId();
      int randomInt = random.nextInt(NUMBER_OF_ITERATIONS);
      int mode = randomInt % 5;
      switch (mode) {
        case 0:
          service.addPancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), orderId, 2);
          service.removePancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), orderId, 1);
          break;
        case 1:
          service.addPancakes(PancakeFactory.getDarkChocolatePancakeRecipe(), orderId, 2);
          service.removePancakes(PancakeFactory.getDarkChocolatePancakeRecipe(), orderId,
              2);
          forciblyCancelled = true;
          break;
        case 2:
          service.addPancakes(PancakeFactory.getDarkChocolateWhippedCreamHazelnutPancakeRecipe(),
              orderId, 2);
          service.removePancakes(PancakeFactory.getMilkChocolatePancakeRecipe(), orderId,
              1);
          break;
        case 3:
          service.addPancakes(PancakeFactory.getDarkChocolateWhippedCreamPancakeRecipe(), orderId,
              2);
          break;
        case 4:
          service.addPancakes(PancakeFactory.getRandomPancakeRecipe(), orderId, 2);
          break;
      }
      if (counter % 3 > 0) {
        service.completeOrder(order);
      } else {
        service.cancelOrder(order);
        if (forciblyCancelled) {
          forciblyCancelled = false;
        }
      }
    } catch (Exception e) {
      LOGGER.info(e.getMessage());
    }
    return forciblyCancelled;
  }


}
