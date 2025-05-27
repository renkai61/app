package com.example.foodapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodapp.db.FoodDatabaseHelper;

public class EditFoodActivity extends AppCompatActivity {
    private EditText editName, editType, editQuantity, editExpiry;
    private int foodId;
    private FoodDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_food);

        editName = findViewById(R.id.editName);
        editType = findViewById(R.id.editType);
        editQuantity = findViewById(R.id.editQuantity);
        editExpiry = findViewById(R.id.editExpiry);
        Button btnSave = findViewById(R.id.btnSave);

        dbHelper = new FoodDatabaseHelper(this);

        // 取得傳來的食物 id
        foodId = getIntent().getIntExtra("foodId", -1);
        if (foodId != -1) {
            loadFoodData(foodId);
        }

        btnSave.setOnClickListener(v -> {
            saveFood();
        });
    }

    private void loadFoodData(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                FoodDatabaseHelper.TABLE_NAME,
                null,
                FoodDatabaseHelper.COLUMN_ID + "=?",
                new String[]{String.valueOf(id)},
                null, null, null
        );

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_NAME));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_TYPE));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_QUANTITY));
            String expiry = cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_EXPIRY_DATE));

            editName.setText(name);
            editType.setText(type);
            editQuantity.setText(String.valueOf(quantity));
            editExpiry.setText(expiry);
        }

        cursor.close();
        db.close();
    }

    private void saveFood() {
        String name = editName.getText().toString().trim();
        String type = editType.getText().toString().trim();
        String quantityStr = editQuantity.getText().toString().trim();
        String expiry = editExpiry.getText().toString().trim();

        if (name.isEmpty() || type.isEmpty() || quantityStr.isEmpty() || expiry.isEmpty()) {
            Toast.makeText(this, "請填寫所有欄位", Toast.LENGTH_SHORT).show();
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "數量必須是數字", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(FoodDatabaseHelper.COLUMN_NAME, name);
        values.put(FoodDatabaseHelper.COLUMN_TYPE, type);
        values.put(FoodDatabaseHelper.COLUMN_QUANTITY, quantity);
        values.put(FoodDatabaseHelper.COLUMN_EXPIRY_DATE, expiry);

        int rows = db.update(FoodDatabaseHelper.TABLE_NAME, values,
                FoodDatabaseHelper.COLUMN_ID + "=?", new String[]{String.valueOf(foodId)});
        db.close();

        if (rows > 0) {
            Toast.makeText(this, "更新成功", Toast.LENGTH_SHORT).show();
            finish(); // 關閉頁面返回上一頁
        } else {
            Toast.makeText(this, "更新失敗", Toast.LENGTH_SHORT).show();
        }
    }
}
