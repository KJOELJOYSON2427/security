package com.joel.messages_service.controller;

import com.joel.messages_service.Repository.MessageRepository;
import com.joel.messages_service.model.Message;
import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
public class MessageController {
    private static final Logger log = LoggerFactory.getLogger(MessageController.class);
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

    @PostMapping("/archive")
//    @PreAuthorize("hasAnyRole('ADMIN')")
    Map<String,String> archiveMessages(){
        System.out.println("came");
        log.info("Archiving all messages");
        return  Map.of("status", "success");
    }
}
