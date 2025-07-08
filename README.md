# Spring Boot FCM Push Notification

This repository provides a sample implementation of sending push notifications using Firebase Cloud Messaging (FCM) in a Spring Boot application.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Setup and Installation](#setup-and-installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Code Explanation](#code-explanation)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## Overview

This project demonstrates how to integrate Firebase Cloud Messaging (FCM) with Spring Boot to send push notifications to Android, iOS, or Web clients. It uses Firebase Admin SDK to authenticate and interact with FCM services.

## Features

- Send push notifications to devices via FCM.
- RESTful API endpoint to trigger notifications.
- FCM server key management via configuration.
- Example notification payloads (title, body, custom data).
- Error handling and logging.

## Architecture

```
[Client] <-- Push Notification -- [FCM Server] <-- [Spring Boot App (This Repo)]
                                                           |
                                                       [REST API]
```

- **Spring Boot Application**: Exposes REST API to accept notification requests.
- **FCM Server**: Receives requests from our app and delivers notifications to devices.
- **Client**: Android/iOS/Web app registered with FCM, receives notifications.

## Setup and Installation

1. **Clone the Repository**
   ```bash
   git clone https://github.com/Vishnukrchy/springboot-fcm-push-notification.git
   cd springboot-fcm-push-notification
   ```

2. **Get Firebase Service Account Key**
   - Go to [Firebase Console](https://console.firebase.google.com/).
   - Create a project (or select an existing one).
   - Navigate to **Project Settings > Service Accounts**.
   - Click **Generate new private key**, download the JSON file.
   - Rename it (e.g. `firebase-service-account.json`) and place it in the `src/main/resources` directory.

3. **Configure Application Properties**
   - Edit `src/main/resources/application.properties`:
     ```
     firebase.config.path=classpath:firebase-service-account.json
     ```

4. **Build the Project**
   ```bash
   ./mvnw clean install
   ```

5. **Run the Application**
   ```bash
   ./mvnw spring-boot:run
   ```
   Or run the generated JAR file in `target/`.

## Configuration

- **Firebase Service Account**: Required to authenticate and send notifications.
- **application.properties**: Set the path to your Firebase credentials.

## Usage

### API Endpoint

- **POST** `/api/notification/send`
  - **Body** (JSON):
    ```json
    {
      "title": "Test Notification",
      "body": "Hello from Spring Boot FCM!",
      "token": "DEVICE_FCM_TOKEN"
    }
    ```
  - Replace `DEVICE_FCM_TOKEN` with the FCM token of your client device.

- **Response**:
  ```json
  {
    "success": true,
    "messageId": "0:1234567890abcdef"
  }
  ```

### Example cURL Request

```bash
curl -X POST http://localhost:8080/api/notification/send \
  -H "Content-Type: application/json" \
  -d '{
        "title": "Test",
        "body": "This is a test notification",
        "token": "YOUR_DEVICE_FCM_TOKEN"
      }'
```

## Code Explanation

### Main Components

#### 1. Firebase Initialization

- Loads service account credentials from the JSON key file.
- Initializes the FirebaseApp instance at application startup.

#### 2. Notification Request Model

- A Java class representing incoming notification requests (title, body, token).

#### 3. Notification Service

- Responsible for:
  - Creating the notification message.
  - Sending the message via FirebaseMessaging.
  - Handling errors and responses.

#### 4. REST Controller

- Exposes API endpoint `/api/notification/send`.
- Accepts a POST request with notification details.
- Calls the Notification Service to send notifications.
- Returns success/failure response.

#### 5. application.properties

- Contains configuration for Firebase credentials.

### File/Folder Structure

```
src/
 └── main/
     ├── java/
     │   └── com/yourcompany/fcm/
     │        ├── controller/      # REST Controller
     │        ├── model/           # Notification request/response models
     │        └── service/         # Notification service logic
     └── resources/
          ├── application.properties
          └── firebase-service-account.json   # (You provide this)
```

### How Each Piece Works Together

- When the app starts, Firebase is initialized with your credentials.
- When you POST to `/api/notification/send` with notification details, the controller receives the request.
- The service creates a notification message and sends it using FirebaseMessaging.
- FCM delivers the message to the device with the provided token.

## Troubleshooting

- **Invalid Credentials**: Ensure the Firebase service account JSON file is correct and in the right location.
- **Invalid Token**: Make sure the FCM token matches a real device/client subscribed to your Firebase project.
- **Port Conflicts**: If port 8080 is in use, change it in `application.properties`.

## Contributing

Contributions are welcome! Please fork the repository and open a pull request.

## License

This project is licensed under the MIT License.

---

**Happy Coding!**
