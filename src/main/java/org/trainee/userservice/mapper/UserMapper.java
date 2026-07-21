package org.trainee.userservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.trainee.userservice.dto.request.UserRequestDto;
import org.trainee.userservice.dto.response.UserResponseDto;
import org.trainee.userservice.model.User;

import java.util.List;

@Mapper(componentModel = "spring", uses = PaymentCardMapper.class)
public interface UserMapper {
    User map(UserRequestDto dto);
    UserResponseDto map(User entity);

    @Mapping(target = "paymentCards", source = "paymentCards")
    List<UserResponseDto> map(List<User> entities);

    @Mapping(target = "createdAt", ignore = true)
    void updateEntityFromDto(UserRequestDto dto, @MappingTarget User entity);
}
