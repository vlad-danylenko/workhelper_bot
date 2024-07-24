package com.danylenko.workhelper.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDto(Long id,
                      String name,
                      String email,
                      List<AddressDto> addresses,
                      AnimalDto animal) {
}
