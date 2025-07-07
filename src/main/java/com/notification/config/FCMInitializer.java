package com.notification.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * Initializes the Firebase SDK.
 * <p>
 * The Firebase configuration file is expected to be in the classpath.
 * The path to this file is specified in the {@code application.properties} file using the
 * {@code app.firebase-configuration-file} property.
 * <p>
 * If the file is not found, the initialization will fail and an error message will be logged.
 * <p>
 * If the Firebase SDK is already initialized, this component will not do anything.
 */
@Service
@Slf4j
public class FCMInitializer {




    @Value("${app.firebase-configuration-file}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            log.info("Initializing Firebase SDK...");
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(
                            new ClassPathResource(firebaseConfigPath).getInputStream()))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                log.info("Initializing Firebase app...");
                FirebaseApp.initializeApp(options);
                log.info("✅ Firebase initialized!");
            } else {
                log.info("Firebase app is already initialized, skipping initialization.");
            }
        } catch (IOException e) {
            log.error("❌ Firebase initialization failed: {}", e.getMessage());
        }
    }
    /**
     * If the secret key file is not set and not exposed in the GitHub repository
     * (e.g. in the .gitignore file), this component will not be able to initialize
     * the Firebase SDK.
     * <p>
     * In this case, the component will not do anything and will not throw an exception.
     * <p>
     * To avoid this situation, make sure to set the path to the secret key file in the
     * {@code application.properties} file using the {@code app.firebase-configuration-file}
     * property.

     */
}