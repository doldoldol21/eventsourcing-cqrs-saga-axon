package com.example.orderservice.command.api.saga;

import com.example.CommonService.commands.CancelOrderCommand;
import com.example.CommonService.commands.CancelPaymentCommand;
import com.example.CommonService.commands.CompleteOrderCommand;
import com.example.CommonService.commands.ShipOrderCommand;
import com.example.CommonService.commands.ValidatePaymentCommand;
import com.example.CommonService.events.OrderCancelledEvent;
import com.example.CommonService.events.OrderCompletedEvent;
import com.example.CommonService.events.OrderShippedEvent;
import com.example.CommonService.events.PaymentCancelledEvent;
import com.example.CommonService.events.PaymentProcessedEvent;
import com.example.CommonService.model.User;
import com.example.CommonService.queries.GetUserPaymentDetailsQuery;
import com.example.orderservice.command.api.events.OrderCreatedEvent;
import java.util.UUID;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Slf4j
public class OrderProcessingSaga {

  @Autowired
  private transient CommandGateway commandGateway;

  @Autowired
  private transient QueryGateway queryGateway;

  @StartSaga
  @SagaEventHandler(associationProperty = "orderId")
  private void handle(OrderCreatedEvent event) {
    log.info("OrderCreateEvent in Saga for Order Id : {}", event.getOrderId());
    GetUserPaymentDetailsQuery getUserPaymentDetailsQuery = new GetUserPaymentDetailsQuery(
      event.getUserId()
    );

    User user = null;

    try {
      user =
        queryGateway
          .query(
            getUserPaymentDetailsQuery,
            ResponseTypes.instanceOf(User.class)
          )
          .join();
    } catch (Exception e) {
      log.error(e.getMessage());
      // 유저 정보 없음
      // Start the Compensating transaction
      // 보상 트랜잭션 시작
      cancelOrderCommand(event.getOrderId());
    }

    ValidatePaymentCommand validatePaymentCommand = ValidatePaymentCommand
      .builder()
      .cardDetails(user.getCardDetails())
      .orderId(event.getOrderId())
      .paymentId(UUID.randomUUID().toString())
      .build();

    commandGateway.sendAndWait(validatePaymentCommand);
  }

  private void cancelOrderCommand(String orderId) {
    CancelOrderCommand cancelOrderCommand = new CancelOrderCommand(orderId);
    commandGateway.send(cancelOrderCommand);
  }

  @SagaEventHandler(associationProperty = "orderId")
  private void handle(PaymentProcessedEvent event) {
    log.info(
      "PaymentProcessedEvent in Saga for Order Id : {}",
      event.getOrderId()
    );
    try {
      if (true) {
        throw new Exception();
      }
      ShipOrderCommand shipOrderCommand = ShipOrderCommand
        .builder()
        .shipmentId(UUID.randomUUID().toString())
        .orderId(event.getOrderId())
        .build();
      commandGateway.sendAndWait(shipOrderCommand);
    } catch (Exception e) {
      log.error(e.getMessage());
      // Start the Compensating transaction
      // 보상 트랜잭션 시작
      cancelPaymentCommand(event);
    }
  }

  private void cancelPaymentCommand(PaymentProcessedEvent event) {
    CancelPaymentCommand cancelPaymentCommand = new CancelPaymentCommand(
      event.getPaymentId(),
      event.getOrderId()
    );
    commandGateway.send(cancelPaymentCommand);
  }

  @SagaEventHandler(associationProperty = "orderId")
  private void handle(OrderShippedEvent event) {
    log.info("OrderShippedEvent in Saga for Order Id : {}", event.getOrderId());

    try {
      CompleteOrderCommand completeOrderCommand = CompleteOrderCommand
        .builder()
        .orderId(event.getOrderId())
        .orderStatus("APPROVED")
        .build();

      commandGateway.sendAndWait(completeOrderCommand);
    } catch (Exception e) {
      // Start the Compensating transaction
      // 보상 트랜잭션 시작
      log.error(e.getMessage());
    }
  }

  @SagaEventHandler(associationProperty = "orderId")
  @EndSaga
  public void handle(OrderCompletedEvent event) {
    log.info(
      "OrderCompletedEvent in Saga for Order Id : {}",
      event.getOrderId()
    );
  }

  @SagaEventHandler(associationProperty = "orderId")
  @EndSaga
  public void handle(OrderCancelledEvent event) {
    log.info(
      "OrderCancelledEvent in Saga for Order Id : {}",
      event.getOrderId()
    );
  }

  @SagaEventHandler(associationProperty = "orderId")
  public void handle(PaymentCancelledEvent event) {
    log.info(
      "PaymentCancelledEvent in Saga for Order Id : {}",
      event.getOrderId()
    );
    cancelOrderCommand(event.getOrderId());
  }
}
