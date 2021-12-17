package com.example.CommonService.queries;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
// 쿼리문은 기본생성자 필요
public class GetUserPaymentDetailsQuery {

  private String userId;
}
