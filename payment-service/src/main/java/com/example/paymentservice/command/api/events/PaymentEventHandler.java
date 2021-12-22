package com.example.paymentservice.command.api.events;

import com.example.CommonService.events.PaymentCancelledEvent;
import com.example.CommonService.events.PaymentProcessedEvent;
import com.example.paymentservice.command.api.data.Payment;
import com.example.paymentservice.command.api.data.PaymentRepository;
import java.util.Date;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PaymentEventHandler {

  @Autowired
  private PaymentRepository paymentRepository;

  @EventHandler
  public void on(PaymentProcessedEvent event) {
    Payment payment = Payment
      .builder()
      .paymentId(event.getPaymentId())
      .orderId(event.getOrderId())
      .paymentStatus("COMPLETED")
      .timeStamp(new Date())
      .build();

    paymentRepository.save(payment);
  }

  @EventHandler
  public void on(PaymentCancelledEvent event) {
    Payment payment = paymentRepository.findById(event.getPaymentId()).get();
    payment.setPaymentStatus(event.getPaymentStatus());
    paymentRepository.save(payment);
  }
}
