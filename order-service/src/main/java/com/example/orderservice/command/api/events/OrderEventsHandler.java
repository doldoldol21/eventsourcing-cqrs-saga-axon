package com.example.orderservice.command.api.events;

import com.example.orderservice.command.api.data.Order;
import com.example.orderservice.command.api.data.OrderRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsHandler {

  @Autowired
  private OrderRepository orderRepository;

  @EventHandler
  public void on(OrderCreatedEvent event) {
    Order order = new Order();
    BeanUtils.copyProperties(event, order);
    orderRepository.save(order);
  }
}