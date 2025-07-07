package com.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "fcm")
public class FCMConfig {
    private int maxRetryAttempts = 3;
    private long retryDelayMs = 1000;
    private long asyncTimeoutSeconds = 10;
    private boolean enableBatchSending = true;
    private int batchSize = 100;
}
