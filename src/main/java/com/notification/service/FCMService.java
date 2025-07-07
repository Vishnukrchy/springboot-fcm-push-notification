package com.notification.service;

import com.google.firebase.messaging.*;
import com.notification.config.FCMConfig;
import com.notification.exception.NotificationException;
import com.notification.model.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@Slf4j
@RequiredArgsConstructor
public class FCMService {
    
    private final FCMConfig fcmConfig;
    private final FirebaseMessaging firebaseMessaging;
    
    @Async
    @Retryable(value = {FirebaseMessagingException.class}, 
               maxAttemptsExpression = "${fcm.max-retry-attempts:3}",
               backoff = @Backoff(delayExpression = "${fcm.retry-delay-ms:1000}"))
    public CompletableFuture<String> sendNotification(NotificationRequest request) {
        log.info("Sending notification with request: {}", request);
        try {
            Message message = request.isSendToTopic() ? buildToTopic(request) : buildToToken(request);
            String response = firebaseMessaging.sendAsync(message)
                .get(fcmConfig.getAsyncTimeoutSeconds(), TimeUnit.SECONDS);
            
            log.info("✅ Successfully sent notification. Message ID: {}", response);
            return CompletableFuture.completedFuture(response);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("❌ Notification sending was interrupted: {}", e.getMessage(), e);
            throw new NotificationException("Notification sending was interrupted", e);
        } catch (TimeoutException e) {
            log.error("❌ Notification sending timed out after {} seconds", fcmConfig.getAsyncTimeoutSeconds(), e);
            throw new NotificationException("Notification sending timed out", e);
        } catch (ExecutionException e) {
            log.error("❌ Failed to send notification: {}", e.getCause() != null ? e.getCause().getMessage() : e.getMessage(), e);
            throw new NotificationException("Failed to send notification", e);
        }
    }
    
    @Recover
    public CompletableFuture<String> recover(FirebaseMessagingException e, NotificationRequest request) {
        log.error("❌ All {} retry attempts failed for notification: {}", 
                fcmConfig.getMaxRetryAttempts(), request, e);
        throw new NotificationException("Failed to send notification after " + 
                fcmConfig.getMaxRetryAttempts() + " attempts", e);
    }



    private Message buildToToken(NotificationRequest request) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody()).build())
                .setAndroidConfig(AndroidConfig.builder()
                        .setTtl(Duration.ofMinutes(2).toMillis())
                        .setPriority(AndroidConfig.Priority.HIGH).build())
                .setApnsConfig(ApnsConfig.builder()
                        .setAps(Aps.builder().setCategory(request.getTopic()).build()).build())
                .setToken(request.getToken())
                .build();
    }

    private Message buildToTopic(NotificationRequest request) {
        return Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getBody()).build())
                .setTopic(request.getTopic())
                .build();
    }

    @Scheduled(cron = "${fcm.daily-notification-cron:0 0 0 * * *}")
    public void sendDailyNotification() {
        log.info("Sending daily notification...");
        NotificationRequest request = NotificationRequest.builder()
                .title("Daily notification")
                .body("This is a daily notification")
                .topic("daily")
                .sendToTopic(true)
                .build();
        sendNotification(request);
    }
}
