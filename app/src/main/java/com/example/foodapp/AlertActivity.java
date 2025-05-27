package com.example.foodapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class AlertActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String foodName = getIntent().getStringExtra("foodName");
        if (foodName == null) foodName = "(未知)"; // 安全預防空值

        new AlertDialog.Builder(this)
                .setTitle("食物過期提醒")
                .setMessage("這裏是foodapp, 【" + foodName + "】即將過期！請盡快食用或處理。")
                .setCancelable(false)
                .setPositiveButton("我知道了", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .show();

    }
}
