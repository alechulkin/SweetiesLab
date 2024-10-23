package org.sweetieslab.service;

import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;
import org.sweetieslab.model.order.Address;
import org.sweetieslab.model.order.Order;
import org.sweetieslab.model.pancakes.Ingredient;
import org.sweetieslab.model.pancakes.PancakeFactory;
import org.sweetieslab.model.pancakes.PancakeRecipe;

public class ManagementService {

    private static final Logger LOGGER = Logger.getLogger(ManagementService.class.getName());

    public static final String CREATED_ORDER_MESSAGE = "Created order %s for building %s, room %s";
    public static final String ADDED_PANCAKES_MESSAGE = """
        Added %d pancake(s) with description '%s' to order %s containing %d pancakes,
         for building %s, room %s""";
    public static final String REMOVED_PANCAKES_MESSAGE = """
        Removed %d pancake(s) with description '%s' from order %s now containing %d pancakes,
         for building %s, room %s""";
    public static final String CANCELLED_ORDER_MESSAGE = """
        Cancelled order %s with %d pancakes for building %s, room %s.""";
    public static final String COMPLETED_ORDER_MESSAGE = """
        Completed order %s with %d pancakes for building %s, room %s.""";
    public static final String PREPARED_ORDER_MESSAGE = """
        Prepared order %s with %d pancakes for building %s, room %s.""";
    public static final String DELIVERED_ORDER_MESSAGE = """
        Order %s with %d pancakes for building %s, room %s out for delivery.""";

    private final OperationsService operationsService;
    private final DataService dataService;
    private final ReentrantLock prepareDeliverLoggingLock = new ReentrantLock();

    public ManagementService(OperationsService operationsService, DataService dataService) {
        this.operationsService = operationsService;
        this.dataService = dataService;
    }

    public Order createOrder(Address address) {
        Order order = new Order(address);
        dataService.addOrder(order);
        LOGGER.info(CREATED_ORDER_MESSAGE.formatted(order.getId(), order.getBuilding(),
            order.getRoom()));
        return order;
    }

    public void addPancakes(PancakeRecipe pancake, UUID orderId, int count) {
        Order order = dataService.addPancakes(pancake, orderId, count);
        LOGGER.info(ADDED_PANCAKES_MESSAGE.formatted(count, pancake, order.getId(),
            order.getPancakesCount(), order.getBuilding(), order.getRoom()));
    }

    public Order getOrder(UUID orderId) {
        return dataService.getOrder(orderId);
    }

    public List<String> viewOrder(UUID orderId) {
        return dataService.getOrder(orderId).getPancakesDescriptions();
    }

    public void removePancakes(EnumMap<Ingredient, Integer> ingredients, UUID orderId, int count) {
        removePancakes(PancakeFactory.getPancakeRecipe(ingredients), orderId, count);
    }

    public void removePancakes(PancakeRecipe pancake, UUID orderId, int count) {
        Order order = dataService.removePancakes(pancake, orderId, count);
        LOGGER.info(REMOVED_PANCAKES_MESSAGE.formatted(count, pancake, order.getId(),
            order.getPancakesCount(), order.getBuilding(), order.getRoom()));
    }

    public void cancelOrder(UUID orderId) {
        Order order = dataService.removeOrder(orderId);
        LOGGER.info(CANCELLED_ORDER_MESSAGE.formatted(order.getId(), order.getPancakesCount(),
            order.getBuilding(), order.getRoom()));
    }

    public synchronized void completeOrder(UUID orderId) {
        Order order = dataService.completeOrder(orderId);
        operationsService.completeOrder(order);
        LOGGER.info(COMPLETED_ORDER_MESSAGE.formatted(order.getId(), order.getPancakesCount(),
            order.getBuilding(), order.getRoom()));
    }

    public Set<UUID> listCompletedOrders() {
        return dataService.listCompletedOrders();
    }

    public Order prepareOrder() {
        Order order = operationsService.prepareOrder();
        dataService.prepareOrder(order.getId());
        prepareDeliverLoggingLock.lock();
        try {
            LOGGER.info(PREPARED_ORDER_MESSAGE.formatted(order.getId(), order.getPancakesCount(),
                order.getBuilding(), order.getRoom()));
        } finally {
            prepareDeliverLoggingLock.unlock();
        }
        return order;
    }

    public Set<UUID> listPreparedOrders() {
        return dataService.listPreparedOrders();
    }

    public Order deliverOrder() {
        Order order = operationsService.deliverOrder();
        UUID orderId = order.getId();
        dataService.removeOrder(orderId);
        prepareDeliverLoggingLock.lock();
        try {
            LOGGER.info(DELIVERED_ORDER_MESSAGE.formatted(order.getId(), order.getPancakesCount(),
                order.getBuilding(), order.getRoom()));
        } finally {
            prepareDeliverLoggingLock.unlock();
        }

        return order;
    }
}
