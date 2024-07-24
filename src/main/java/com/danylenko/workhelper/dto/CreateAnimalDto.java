package com.danylenko.workhelper.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CreateAnimalDto(String name) {
}
