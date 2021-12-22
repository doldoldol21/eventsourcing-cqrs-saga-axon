package com.example.shipmentservice.command.api.aggregate;

import com.example.CommonService.commands.ShipOrderCommand;
import com.example.CommonService.events.OrderShippedEvent;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.modelling.command.AggregateLifecycle;
import org.axonframework.spring.stereotype.Aggregate;

@NoArgsConstructor
@Aggregate
public class ShipmentAggregate {

  @AggregateIdentifier
  private String shipmentId;

  private String orderId;
  private String shipmentStatus;

  @CommandHandler
  public ShipmentAggregate(ShipOrderCommand shipOrderCommand) {
    // Validate the Command
    // Publish the Order Shipped event
    OrderShippedEvent orderShippedEvent = OrderShippedEvent
      .builder()
      .shipmentId(shipOrderCommand.getShipmentId())
      .orderId(shipOrderCommand.getOrderId())
      .shipmentStatus("COMPLETED")
      .build();

    AggregateLifecycle.apply(orderShippedEvent);
  }

  @EventSourcingHandler
  public void on(OrderShippedEvent event) {
    this.shipmentId = event.getShipmentId();
    this.orderId = event.getOrderId();
    this.shipmentStatus = event.getShipmentStatus();
  }
}
