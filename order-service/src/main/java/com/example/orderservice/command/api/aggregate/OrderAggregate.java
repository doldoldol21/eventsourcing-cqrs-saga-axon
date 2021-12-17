package com.example.orderservice.command.api.aggregate;

import com.example.orderservice.command.api.command.CreateOrderCommand;
import com.example.orderservice.command.api.events.OrderCreatedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
// 도메인의 집합 (Aggregate)
// 확장을 위해서 반드시 봐야할 곳
public class OrderAggregate {

  @AggregateIdentifier
  private String orderId;

  private String productId;
  private String userId;
  private String addressId;
  private Integer quantity;
  private String orderStatus;

  // Handler Annotation 꼭 필요함
  @CommandHandler
  public OrderAggregate(CreateOrderCommand createOrderCommand) {
    // CommandGateway Send 에서 여기로 온다.
    // 유효성 검사 후 이벤트 발생.
    OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
    BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
    AggregateLifecycle.apply(orderCreatedEvent);
  }

  // Event Handler
  @EventHandler
  public void on(OrderCreatedEvent event) {
    this.orderId = event.getOrderId();
    this.productId = event.getProductId();
    this.userId = event.getUserId();
    this.quantity = event.getQuantity();
    this.addressId = event.getAddressId();
    this.orderStatus = event.getOrderStatus();
  }
}
