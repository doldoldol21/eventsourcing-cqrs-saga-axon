package com.example.shipmentservice.command.api.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.Builder;
import lombok.Data;

@Data
@Entity
public class Shipment {

  @Id
  private String shipmentId;

  private String orderId;
  private String shipmentStatus;
}
