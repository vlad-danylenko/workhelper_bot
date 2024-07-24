package com.danylenko.workhelper.dto;

import com.danylenko.workhelper.entity.Animal;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateUserDto(
        @NotEmpty(message = "Name can't be empty")
        String name,
        String email,
        List<CreateAddressDto> addresses,
        CreateAnimalDto animal) {
}
