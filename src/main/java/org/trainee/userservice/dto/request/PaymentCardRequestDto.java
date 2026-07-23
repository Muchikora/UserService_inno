package org.trainee.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @Positive
    private Long userId;

    @NotBlank
    private String number;

    @NotBlank
    private String holder;

    @NotNull
    private LocalDate expirationDate;
}
