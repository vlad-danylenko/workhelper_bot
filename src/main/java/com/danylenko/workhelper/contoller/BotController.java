package com.danylenko.workhelper.contoller;

import com.danylenko.workhelper.dto.MessageRequestDto;
import com.danylenko.workhelper.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BotController {
    private final MessageService messageService;

    public BotController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    public ResponseEntity<Void> sendProactiveMessage(@RequestBody MessageRequestDto request) {
        messageService.sendMessage(request.getChatId(), request.getResponseText(), request.isKeyboard());
        return ResponseEntity.ok().build();
    }
}
