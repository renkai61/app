package com.example.foodapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class StartActivity extends AppCompatActivity {
    private static final String TAG = "StartActivity";
    private void initToolbar() {
        try {
            Toolbar toolbar = findViewById(R.id.toolbar);
            if (toolbar != null) {
                setSupportActionBar(toolbar);
                Log.d(TAG, "Toolbar set as ActionBar");
                // Set title programmatically
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("🍱 食物管理系統");
                }
            } else {
                Log.w(TAG, "Toolbar not found in layout. Skipping ActionBar setup.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing toolbar: " + e.getMessage(), e);
            Log.w(TAG, "Toolbar initialization failed. Continuing without ActionBar.");
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("isLoggedIn", false);

        if (isLoggedIn) {
            // 使用者已登入，跳轉到主畫面
            startActivity(new Intent(this, MainActivity.class));
        } else {
            // 尚未登入，跳轉到登入頁
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish(); // 關閉 StartActivity
    }

}

