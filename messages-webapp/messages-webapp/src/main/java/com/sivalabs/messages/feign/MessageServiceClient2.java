package com.sivalabs.messages.feign;


import com.sivalabs.messages.DTO.Message;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "message-service", url = "${message.service.url}")
public interface MessageServiceClient2  {


    @GetMapping("/api/messages")
    List<Message> getMessages();

    @PostMapping("/api/messages")
    Message createMessage(
            @RequestBody  Message message,
            @RequestHeader("Authorization") String authHeader
            );
}
