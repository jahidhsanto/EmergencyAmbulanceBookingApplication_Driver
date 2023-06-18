package com.example.emergencyambulancebookingapplication_driver;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage message) {
        super.onMessageReceived(message);
        // Check if the message contains a data payload
        if (message.getData().size() > 0) {
            // Handle the custom data
            String senderUID = message.getData().get("senderUId");
            // You can now use the custom data as needed, such as displaying it in an activity or a notification
            Driver_Accept.senderUserID = senderUID;
        }

        // Check if the message contains a notification payload
        if (message.getNotification() != null) {
            // Handle the notification
            String title = message.getNotification().getTitle();
            String body = message.getNotification().getBody();
            // Display the notification
            NotificationHelper.displayNotification(MainActivity.mainActivityContext, title, body);
        }
    }
}
