package com.example.orderservice.command.api.controller;

import com.example.orderservice.command.api.command.CreateOrderCommand;
import com.example.orderservice.command.api.model.OrderRestModel;
import java.util.UUID;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderCommandController {

  @Autowired
  private CommandGateway commandGateway;

  @PostMapping
  public String createOrder(@RequestBody OrderRestModel orderRestModel) {
    CreateOrderCommand createOrderCommand = CreateOrderCommand
      .builder()
      .orderId(UUID.randomUUID().toString())
      .userId(orderRestModel.getUserId())
      .productId(orderRestModel.getProductId())
      .addressId(orderRestModel.getAddressId())
      .quantity(orderRestModel.getQuantity())
      .orderStatus("CREATED")
      .build();

    commandGateway.sendAndWait(createOrderCommand);

    return "Order Created";
  }
}
