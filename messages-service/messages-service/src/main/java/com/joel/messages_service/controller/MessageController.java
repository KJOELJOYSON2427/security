package com.joel.messages_service.controller;

import com.joel.messages_service.Repository.MessageRepository;
import com.joel.messages_service.model.Message;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private final MessageRepository messageRepository;

    MessageController(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @GetMapping
    List<Message> getMessages(){
        return messageRepository.getAllMessages();
    }

    @PostMapping
    Message createMessage(@RequestBody @Valid Message message){
        return  messageRepository.createMessage(message);
    }
}
