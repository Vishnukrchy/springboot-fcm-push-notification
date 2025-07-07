package com.notification.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TokenRegistrationRequest {
    @NotBlank(message = "Device token is required")
    private String token;
}
