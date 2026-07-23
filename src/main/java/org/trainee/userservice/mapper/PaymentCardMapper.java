package org.trainee.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trainee.userservice.dto.request.PaymentCardRequestDto;
import org.trainee.userservice.dto.response.PaymentCardResponseDto;
import org.trainee.userservice.model.PaymentCard;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {
    PaymentCard map(PaymentCardRequestDto dto);

    @Mapping(target = "userId", source = "user.id")
    PaymentCardResponseDto map(PaymentCard entity);

    @Mapping(target = "userId", source = "user.id")
    List<PaymentCardResponseDto> map(List<PaymentCard> entities);

    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(PaymentCardRequestDto dto, @MappingTarget PaymentCard entity);
}
