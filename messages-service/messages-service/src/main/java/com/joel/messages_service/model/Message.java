package com.joel.messages_service.model;

import jakarta.validation.constraints.NotEmpty;

import java.time.Instant;

public class Message {

    private Long id;
    @NotEmpty
    private String content;

    @NotEmpty
    private String CreatedBy;

    private Instant CreatedAt;

    public Message(Long id, String content, String createdBy, Instant createdAt) {
        this.id = id;
        this.content = content;
        CreatedBy = createdBy;
        CreatedAt = createdAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreatedBy() {
        return CreatedBy;
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public Instant getCreatedAt() {
        return CreatedAt;
    }

    public void setCreatedAt(Instant createdAt) {
        CreatedAt = createdAt;
    }
}
