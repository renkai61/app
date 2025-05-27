package com.example.foodapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodapp.db.UserDatabaseHelper;  // <---- 這行一定要加

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegisterConfirm;
    private UserDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        dbHelper = new UserDatabaseHelper(this);

        etUsername = findViewById(R.id.editUsername);
        etPassword = findViewById(R.id.editPassword);
        etConfirmPassword = findViewById(R.id.editConfirmPassword); // 確認密碼欄位
        btnRegisterConfirm = findViewById(R.id.btnRegisterConfirm);

        btnRegisterConfirm.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPassword = etConfirmPassword.getText().toString().trim();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "帳號密碼不能為空", Toast.LENGTH_SHORT).show();
                return;
            }

            if (username.length() < 3) {
                Toast.makeText(this, "帳號至少需要3個字元", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 4) {
                Toast.makeText(this, "密碼至少需要4個字元", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "密碼與確認密碼不一致", Toast.LENGTH_SHORT).show();
                return;
            }

            // 檢查是否有同帳號
            if (dbHelper.checkUser(username)) {
                Toast.makeText(this, "帳號已存在，請選擇其他帳號", Toast.LENGTH_LONG).show();
                return;
            }

            // 新增使用者
            boolean success = dbHelper.addUser(username, password);
            if (success) {
                Toast.makeText(this, "註冊成功！即將跳轉到登入頁面", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra("username", username);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "註冊失敗，請稍後再試", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
