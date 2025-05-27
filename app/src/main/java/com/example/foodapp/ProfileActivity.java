package com.example.foodapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {
    private EditText etName, etEmail, etPhone;
    private Button btnSave, btnLogout;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);  // 登出按鈕

        sharedPreferences = getSharedPreferences("UserProfile", MODE_PRIVATE);
        loadProfile();

        btnSave.setOnClickListener(v -> saveProfile());

        btnLogout.setOnClickListener(v -> {
            // 清除登入 SharedPreferences，這邊示範假設登入資訊存在 user_prefs
            SharedPreferences loginPrefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = loginPrefs.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(ProfileActivity.this, "已登出", Toast.LENGTH_SHORT).show();

            // 跳轉到登入頁面並清除返回堆疊
            Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void loadProfile() {
        etName.setText(sharedPreferences.getString("name", ""));
        etEmail.setText(sharedPreferences.getString("email", ""));
        etPhone.setText(sharedPreferences.getString("phone", ""));
    }

    private void saveProfile() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", etName.getText().toString());
        editor.putString("email", etEmail.getText().toString());
        editor.putString("phone", etPhone.getText().toString());
        editor.apply();
        Toast.makeText(this, "資料已儲存", Toast.LENGTH_SHORT).show();
    }
}
