package com.danylenko.workhelper.mapper;

import com.danylenko.workhelper.dto.CreateUserDto;
import com.danylenko.workhelper.dto.UserDto;
import com.danylenko.workhelper.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toModel(CreateUserDto createUserDto);
}
