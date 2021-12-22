package com.example.paymentservice.command.api.aggregate;

import com.example.CommonService.commands.CancelPaymentCommand;
import com.example.CommonService.commands.ValidatePaymentCommand;
import com.example.CommonService.events.PaymentCancelledEvent;
import com.example.CommonService.events.PaymentProcessedEvent;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

@NoArgsConstructor
@Aggregate
@Slf4j
public class PaymentAggregate {

  @AggregateIdentifier
  private String paymentId;

  private String orderId;
  private String paymentStatus;

  @CommandHandler
  public PaymentAggregate(ValidatePaymentCommand validatePaymentCommand) {
    // Validate the Payment Details
    // Publish the Payment Processed event
    log.info(
      "Executing ValidatePaymentCommand for" +
      "Order Id: {} and Payment Id: {}",
      validatePaymentCommand.getOrderId(),
      validatePaymentCommand.getPaymentId()
    );

    PaymentProcessedEvent paymentProcessedEvent = new PaymentProcessedEvent(
      validatePaymentCommand.getPaymentId(),
      validatePaymentCommand.getOrderId()
    );

    AggregateLifecycle.apply(paymentProcessedEvent);

    log.info("PaymentProcessedEvent Applied");
  }

  @EventSourcingHandler
  public void on(PaymentProcessedEvent paymentProcessedEvent) {
    this.paymentId = paymentProcessedEvent.getPaymentId();
    this.orderId = paymentProcessedEvent.getOrderId();
  }

  @CommandHandler
  public void handle(CancelPaymentCommand cancelPaymentCommand) {
    PaymentCancelledEvent paymentCancelledEvent = new PaymentCancelledEvent();
    BeanUtils.copyProperties(cancelPaymentCommand, paymentCancelledEvent);
    AggregateLifecycle.apply(paymentCancelledEvent);
  }

  @EventSourcingHandler
  public void on(PaymentCancelledEvent event) {
    this.paymentStatus = event.getPaymentStatus();
  }
}
