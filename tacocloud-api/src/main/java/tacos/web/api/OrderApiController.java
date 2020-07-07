package tacos.web.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tacos.Order;
import tacos.data.OrderRepository;
import tacos.messaging.OrderMessagingService;

@RestController
@RequestMapping(path = "/orders", produces = "application/json")
@CrossOrigin(origins = "*")
public class OrderApiController {

    private OrderRepository orderRepository;
    private OrderMessagingService orderMessagingService;

    @Autowired
    public OrderApiController(OrderRepository orderRepository, OrderMessagingService orderMessagingService) {
        this.orderRepository = orderRepository;
        this.orderMessagingService = orderMessagingService;
    }

    @GetMapping(produces = "application/json")
    public Iterable<Order> allOrders() {
        return orderRepository.findAll();
    }

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Order postOrder(@RequestBody Order order) {
        orderMessagingService.sendOrder(order);
        return orderRepository.save(order);
    }

    @PutMapping(path = "/{orderId}", consumes = "application/json")
    public Order putOrder(@RequestBody Order order) {
        return orderRepository.save(order);
    }

    @PatchMapping(path = "/{orderId}", consumes = "application/json")
    public Order patchOrder(@PathVariable("orderId") Long orderId, @RequestBody Order patchOrder) {
        Order order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            return null;
        }

        if (patchOrder.getDeliveryName() != null) {
            order.setDeliveryName(patchOrder.getDeliveryName());
        }
        if (patchOrder.getDeliveryStreet() != null) {
            order.setDeliveryStreet(patchOrder.getDeliveryStreet());
        }
        if (patchOrder.getDeliveryCity() != null) {
            order.setDeliveryCity(patchOrder.getDeliveryCity());
        }
        if (patchOrder.getDeliveryState() != null) {
            order.setDeliveryState(patchOrder.getDeliveryState());
        }
        if (patchOrder.getDeliveryZip() != null) {
            order.setDeliveryZip(patchOrder.getDeliveryState());
        }
        if (patchOrder.getCcNumber() != null) {
            order.setCcNumber(patchOrder.getCcNumber());
        }
        if (patchOrder.getCcExpiration() != null) {
            order.setCcExpiration(patchOrder.getCcExpiration());
        }
        if (patchOrder.getCcCVV() != null) {
            order.setCcCVV(patchOrder.getCcCVV());
        }

        return orderRepository.save(order);
    }

    @DeleteMapping(path = "/{orderId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable("orderId") Long orderId) {
        try {
            orderRepository.deleteById(orderId);
        } catch (EmptyResultDataAccessException ignored) {}
    }

}
