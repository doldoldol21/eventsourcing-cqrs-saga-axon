package com.example.orderservice.command.api.events;

import com.example.CommonService.events.OrderCancelledEvent;
import com.example.CommonService.events.OrderCompletedEvent;
import com.example.orderservice.command.api.data.Order;
import com.example.orderservice.command.api.data.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OrderEventsHandler {

  @Autowired
  private OrderRepository orderRepository;

  @EventHandler
  public void on(OrderCreatedEvent event) {
    Order order = new Order();
    BeanUtils.copyProperties(event, order);

    log.info("OrderCreateEvent ! repository save {}", order);

    orderRepository.save(order);
  }

  @EventHandler
  public void on(OrderCompletedEvent event) {
    Order order = orderRepository.findById(event.getOrderId()).get();
    order.setOrderStatus(event.getOrderStatus());
    orderRepository.save(order);
  }

  @EventHandler
  public void on(OrderCancelledEvent event) {
    Order order = orderRepository.findById(event.getOrderId()).get();
    order.setOrderStatus(event.getOrderStatus());
    orderRepository.save(order);
  }
}
