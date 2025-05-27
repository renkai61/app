package com.example.foodapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ExpiryReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String foodName = intent.getStringExtra("foodName");

        Intent alertIntent = new Intent(context, AlertActivity.class);
        alertIntent.putExtra("foodName", foodName); // 這行不能漏
        alertIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alertIntent);

    }
}
