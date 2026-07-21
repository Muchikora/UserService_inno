package org.trainee.userservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentCardRequestDto {
    private Integer userId;
    private String number;
    private String holder;
    private LocalDate expirationDate;
}
