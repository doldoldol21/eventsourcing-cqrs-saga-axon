package com.example.orderservice.command.api.saga;

import com.example.CommonService.commands.ValidatePaymentCommand;
import com.example.CommonService.model.User;
import com.example.CommonService.queries.GetUserPaymentDetailsQuery;
import com.example.orderservice.command.api.events.OrderCreatedEvent;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.queryhandling.QueryGateway;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;

@Saga
@Slf4j
public class OrderProcessingSaga {

  @Autowired
  private CommandGateway commandGateway;

  @Autowired
  private QueryGateway queryGateway;

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

    }

    ValidatePaymentCommand validatePaymentCommand = ValidatePaymentCommand
      .builder()
      .cardDetails(user.getCardDetails())
      .orderId(event.getOrderId())
      .paymentId(UUID.randomUUID().toString())
      .build();
    //commandGateway.send
  }
}
