package com.example.emergencyambulancebookingapplication_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth fAuth;
    public static final String CHANNEL_ID = "simplified_coding";
    private static final String CHANNEL_NAME = "simplified Coding";
    private static final String CHANNEL_DESC = "simplified Coding Notifications";
    public static Context mainActivityContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainActivityContext = getApplicationContext();

        // Initialize Firebase Auth
        fAuth = FirebaseAuth.getInstance();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(CHANNEL_DESC);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        // Check if user is signed in (non-null)
        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            finish();
        }
    }

    public void signin(View view) {
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
    }

    public void signup(View view) {
        startActivity(new Intent(MainActivity.this, SignUpActivity_1.class));
    }
}