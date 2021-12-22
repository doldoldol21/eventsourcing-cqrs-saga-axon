package com.example.userservice.projection;

import com.example.CommonService.model.CardDetails;
import com.example.CommonService.model.User;
import com.example.CommonService.queries.GetUserPaymentDetailsQuery;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
public class UserProjection {

  @QueryHandler
  public User getUserPaymentDetails(GetUserPaymentDetailsQuery query) {
    System.out.println("aymentDetails !!!");
    // DB에서 요청 해야함 (query.getUserId())
    // Ideally Get the details from the DB
    CardDetails cardDetails = CardDetails
      .builder()
      .name("Haeje Park")
      .validUntilYear(2025)
      .validUntilMonth(05)
      .cardNumber("123456789")
      .cvv(155)
      .build();

    return User
      .builder()
      .userId(query.getUserId())
      .firstName("Haeje")
      .lastName("Park")
      .cardDetails(cardDetails)
      .build();
  }
}
