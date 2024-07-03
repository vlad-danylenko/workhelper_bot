package com.danylenko.workhelper.dto;

import lombok.Data;

@Data
public class MessageRequestDto {
    private long chatId;
    private String responseText;
    private boolean keyboard;
}
