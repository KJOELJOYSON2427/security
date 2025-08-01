package com.sivalabs.messages.service;

import com.sivalabs.messages.DTO.Message;

import com.sivalabs.messages.feign.MessageServiceClient2;
import com.sivalabs.messages.helper.SecurityHepler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageServiceClient {
     private final MessageServiceClient2 messageServiceClient;
     private final SecurityHepler securityHepler;
    private  static  final Logger log = LoggerFactory.getLogger(MessageServiceClient.class);
    public MessageServiceClient(MessageServiceClient2 messageServiceClient, SecurityHepler securityHepler) {
        this.messageServiceClient = messageServiceClient;
        this.securityHepler = securityHepler;
    }

    public List<Message> getMessages(){
        try{
            log.error("came to fetch from client");
             return messageServiceClient.getMessages();
        }catch (Exception e){
            log.error("Error while fetching messages", e);
            return List.of();
        }
    }


    public  void createMessage(Message message){
    try{
        String token = "Bearer " + securityHepler.getAccessToken();
        Message created = messageServiceClient.createMessage(message,token);
        log.info("Message created successfully: {}", created);

    } catch (Exception e) {
        log.error("Error while creating message", e);
    }
    }
}
