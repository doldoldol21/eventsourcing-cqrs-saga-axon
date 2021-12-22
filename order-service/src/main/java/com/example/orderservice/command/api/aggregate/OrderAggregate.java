package com.example.orderservice.command.api.aggregate;

import com.example.CommonService.commands.CancelOrderCommand;
import com.example.CommonService.commands.CompleteOrderCommand;
import com.example.CommonService.events.OrderCancelledEvent;
import com.example.CommonService.events.OrderCompletedEvent;
import com.example.orderservice.command.api.command.CreateOrderCommand;
import com.example.orderservice.command.api.events.OrderCreatedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@Aggregate
@NoArgsConstructor
@Slf4j
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
    log.info("userInfo CreateOrderCommand: {}", createOrderCommand);
    OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent();
    BeanUtils.copyProperties(createOrderCommand, orderCreatedEvent);
    AggregateLifecycle.apply(orderCreatedEvent);
  }

  // Event Handler
  @EventSourcingHandler
  public void on(OrderCreatedEvent event) {
    this.orderId = event.getOrderId();
    this.productId = event.getProductId();
    this.userId = event.getUserId();
    this.quantity = event.getQuantity();
    this.addressId = event.getAddressId();
    this.orderStatus = event.getOrderStatus();
  }

  @CommandHandler
  public void handle(CompleteOrderCommand completeOrderCommand) {
    // Validate The Command
    // Publish Order Completed Event
    OrderCompletedEvent orderCompletedEvent = OrderCompletedEvent
      .builder()
      .orderId(completeOrderCommand.getOrderId())
      .orderStatus(completeOrderCommand.getOrderStatus())
      .build();

    AggregateLifecycle.apply(orderCompletedEvent);
  }

  @EventSourcingHandler
  public void on(OrderCompletedEvent event) {
    this.orderStatus = event.getOrderStatus();
  }

  @CommandHandler
  public void handle(CancelOrderCommand cancelOrderCommand) {
    OrderCancelledEvent orderCancelledEvent = new OrderCancelledEvent();
    BeanUtils.copyProperties(cancelOrderCommand, orderCancelledEvent);

    AggregateLifecycle.apply(orderCancelledEvent);
  }

  @EventSourcingHandler
  public void on(OrderCancelledEvent event) {
    this.orderStatus = event.getOrderStatus();
  }
}
