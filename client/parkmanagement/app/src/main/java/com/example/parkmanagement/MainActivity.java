package com.example.parkmanagement; // Replace with your actual package name

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final long DELAY_TIME_MS = 3000; // 3 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Set the content view to activity_main.xml

        // Delay for 3 seconds before transitioning to UserLoginActivity
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                // Create an Intent to start UserLoginActivity
                Intent intent = new Intent(MainActivity.this, UserLoginActivity.class);
                startActivity(intent);
                finish(); // Call finish() to close the current activity
            }
        }, DELAY_TIME_MS);
    }
}