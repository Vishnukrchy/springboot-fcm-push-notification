package com.notification.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TopicNotificationRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Body is required")
    private String body;
    
    @NotBlank(message = "Topic is required")
    private String topic;
}
