package com.joel.messages_service.Repository;

import jakarta.annotation.PostConstruct;
import com.joel.messages_service.model.Message;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
@Repository
public class MessageRepository {

    private  static final AtomicLong Id = new AtomicLong(0L);
    private static final List<Message> MESSAGES = new ArrayList<>();


@PostConstruct
void init() {
    getDefaultMessages().forEach(
            p->{
                p.setId(Id.incrementAndGet());
                MESSAGES.add(p);
            }
    );
}
    public List<Message> getAllMessages(){
        return  MESSAGES;
    }

    public  Message createMessage(Message message){
         message.setId(Id.incrementAndGet());
         message.setCreatedAt(Instant.now());
         MESSAGES.add(message);
         return  message;
    }

    private List<Message> getDefaultMessages(){
           List<Message> messages = new ArrayList<>();
           messages.add(new Message(null, "Test Message 1", "admin", Instant.now()));
                   messages.add(new Message(null, "Test Message 2", "admin", Instant.now()));
        return messages;

    }

}
