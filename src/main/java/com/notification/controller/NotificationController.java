package com.notification.controller;

import com.notification.exception.NotificationException;
import com.notification.model.*;
import com.notification.repository.DeviceTokenRepository;
import com.notification.service.FCMService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final FCMService fcmService;
    private final DeviceTokenRepository tokenRepo;

    @PostMapping("/send")
    public DeferredResult<ResponseEntity<NotificationResponse>> sendNotification(
            @Valid @RequestBody NotificationRequest request) {
        
        log.info("Received notification request: {}", request);
        DeferredResult<ResponseEntity<NotificationResponse>> deferredResult = new DeferredResult<>();
        
        fcmService.sendNotification(request)
            .thenAccept(response -> {
                log.info("Notification sent successfully. Message ID: {}", response);
                deferredResult.setResult(ResponseEntity.ok(
                    new NotificationResponse(HttpStatus.OK.value(), "Notification sent successfully!")
                ));
            })
            .exceptionally(ex -> {
                log.error("Failed to send notification", ex);
                deferredResult.setErrorResult(ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Failed to send notification: " + ex.getCause().getMessage()
                    )));
                return null;
            });
            
        return deferredResult;
    }
    
    @PostMapping("/send-to-topic")
    public DeferredResult<ResponseEntity<NotificationResponse>> sendToTopic(
            @Valid @RequestBody TopicNotificationRequest request) {
                
        log.info("Sending topic notification: {}", request);
        return sendNotification(NotificationRequest.builder()
            .title(request.getTitle())
            .body(request.getBody())
            .topic(request.getTopic())
            .sendToTopic(true)
            .build());
    }
    
    @PostMapping("/send-to-device")
    public DeferredResult<ResponseEntity<NotificationResponse>> sendToDevice(
            @Valid @RequestBody DeviceNotificationRequest request) {
                
        log.info("Sending device notification: {}", request);
        return sendNotification(NotificationRequest.builder()
            .title(request.getTitle())
            .body(request.getBody())
            .token(request.getDeviceToken())
            .sendToTopic(false)
            .build());
    }

    @PostMapping("/register-token")
    public ResponseEntity<ApiResponse> registerToken(@Valid @RequestBody TokenRegistrationRequest request) {
        log.info("Registering device token: {}", request.getToken());
        try {
            tokenRepo.addToken(request.getToken());
            return ResponseEntity.ok(new ApiResponse(true, "Token registered successfully"));
        } catch (Exception e) {
            log.error("Failed to register token: {}", request.getToken(), e);
            throw new NotificationException("Failed to register token");
        }
    }

    @GetMapping("/tokens")
    public ResponseEntity<Set<String>> getTokens() {
        log.info("Fetching all registered tokens");
        return ResponseEntity.ok(tokenRepo.getAllTokens());
    }
    
    @DeleteMapping("/tokens/{token}")
    public ResponseEntity<ApiResponse> removeToken(@PathVariable String token) {
        log.info("Removing token: {}", token);
        try {
            tokenRepo.removeToken(token);
            return ResponseEntity.ok(new ApiResponse(true, "Token removed successfully"));
        } catch (Exception e) {
            log.error("Failed to remove token: {}", token, e);
            throw new NotificationException("Failed to remove token");
        }
    }
}
