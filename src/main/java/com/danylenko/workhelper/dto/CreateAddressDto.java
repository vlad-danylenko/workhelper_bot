package com.danylenko.workhelper.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)

public record CreateAddressDto(String street,
                               String city) {
}
