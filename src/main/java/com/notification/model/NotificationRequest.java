package com.notification.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationRequest {
    @NotBlank private String title;
    @NotBlank private String body;
    private String topic;
    private String token;
    private boolean sendToTopic;  // true = send to topic; false = send to token
}
