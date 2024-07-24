package com.danylenko.workhelper.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AddressDto(
        @JsonIgnore
        Long id,
        String street,
        String city) {
}
