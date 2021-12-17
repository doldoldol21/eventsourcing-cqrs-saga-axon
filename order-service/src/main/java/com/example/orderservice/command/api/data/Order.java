package com.example.orderservice.command.api.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "orders")
public class Order {

  //Order DTO

  @Id
  private String orderId;

  private String productId;
  private String userId;
  private String addressId;
  private Integer quantity;
  private String orderStatus;
}
