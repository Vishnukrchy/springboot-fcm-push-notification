package com.notification.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DeviceNotificationRequest {
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Body is required")
    private String body;
    
    @NotBlank(message = "Device token is required")
    private String deviceToken;
}
