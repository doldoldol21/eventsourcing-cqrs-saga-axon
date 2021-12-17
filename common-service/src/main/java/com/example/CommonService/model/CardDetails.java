package com.example.CommonService.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
// 카드 정보
public class CardDetails {

  private String name;
  private String cardNumber;
  private Integer validUntilMonth;
  private Integer validUntilYear;
  private Integer cvv;
}
