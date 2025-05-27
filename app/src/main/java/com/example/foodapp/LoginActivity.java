package com.example.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodapp.db.UserDatabaseHelper;  // <---- 這行一定要加
import android.content.SharedPreferences;

public class LoginActivity extends AppCompatActivity {

    private EditText editUsername;
    private EditText editPassword;
    private Button btnLogin, btnGoToRegister;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new UserDatabaseHelper(this);

        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnGoToRegister = findViewById(R.id.btnGoToRegister);

        Intent intent = getIntent();
        String usernameFromRegister = intent.getStringExtra("username");
        if (usernameFromRegister != null && !usernameFromRegister.isEmpty()) {
            editUsername.setText(usernameFromRegister);
            editPassword.requestFocus();
            Toast.makeText(this, "請輸入密碼完成登入", Toast.LENGTH_SHORT).show();
        }

        btnLogin.setOnClickListener(v -> {
            String username = editUsername.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "請輸入帳號和密碼", Toast.LENGTH_SHORT).show();
                return;
            }

            // 檢查使用者是否存在且密碼正確
            if (!dbHelper.checkUser(username)) {
                Toast.makeText(this, "未找到註冊帳號，請先註冊", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(this, RegisterActivity.class));
                return;
            }

            if (dbHelper.checkUserCredentials(username, password)) {
                Toast.makeText(this, "登入成功", Toast.LENGTH_SHORT).show();

                SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
                prefs.edit().putBoolean("isLoggedIn", true)
                        .putString("username", username) // 可選，儲存登入用戶名
                        .apply();

                Intent mainIntent = new Intent(this, MainActivity.class);
                mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(mainIntent);
                finish();
            } else {
                Toast.makeText(this, "帳號或密碼錯誤", Toast.LENGTH_SHORT).show();
            }
        });

        btnGoToRegister.setOnClickListener(v -> {
            Intent registerIntent = new Intent(this, RegisterActivity.class);
            startActivity(registerIntent);
        });
    }
}
