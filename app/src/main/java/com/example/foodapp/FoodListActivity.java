package com.example.foodapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodapp.db.FoodDatabaseHelper;

import com.example.foodapp.FoodListAdapter;
import java.util.ArrayList;

public class FoodListActivity extends AppCompatActivity {
    ListView listView;
    ArrayList<FoodItem> foodList = new ArrayList<>();
    FoodListAdapter adapter;   // 用自訂 Adapter
    ArrayList<Integer> foodIds = new ArrayList<>(); // 仍保留 id，方便查詢

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_list);

        listView = findViewById(R.id.listView);
        loadData(true, "");
        SearchView searchView = findViewById(R.id.searchView);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            int foodId = foodList.get(position).id;

            Intent intent = new Intent(FoodListActivity.this, EditFoodActivity.class);
            intent.putExtra("foodId", foodId);
            startActivity(intent);
        });

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            int foodId = foodList.get(position).id;
            new AlertDialog.Builder(this)
                    .setTitle("刪除確認")
                    .setMessage("確定要刪除這筆資料嗎？")
                    .setPositiveButton("是", (dialog, which) -> {
                        deleteFoodById(foodId);
                        loadData(true, "");
                    })
                    .setNegativeButton("否", null)
                    .show();
            return true;
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) { return false; }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadData(true, newText);
                return true;
            }
        });
    }

    private void loadData(boolean sortByExpiry, String searchKeyword) {
        foodList.clear();
        foodIds.clear();

        FoodDatabaseHelper dbHelper = new FoodDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;
        String selection = null;
        String[] selectionArgs = null;

        if (searchKeyword != null && !searchKeyword.isEmpty()) {
            selection = FoodDatabaseHelper.COLUMN_NAME + " LIKE ?";
            selectionArgs = new String[]{"%" + searchKeyword + "%"};
        }

        String orderBy = sortByExpiry ? FoodDatabaseHelper.COLUMN_EXPIRY_DATE + " ASC" : null;

        cursor = db.query(
                FoodDatabaseHelper.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                orderBy
        );

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_ID));
            String name = cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_NAME));
            String type = cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_TYPE));
            int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_QUANTITY));
            String expiry = cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_EXPIRY_DATE));
            String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_PHOTO_URI));
            // 注意：你的資料庫需有一欄存圖片路徑，名稱可調整，或你用其他方式存圖片

            foodList.add(new FoodItem(id, name, type, quantity, expiry, imagePath));
        }

        cursor.close();
        db.close();

        adapter = new FoodListAdapter(this, foodList);
        listView.setAdapter(adapter);
    }

    private void deleteFoodById(int id) {
        FoodDatabaseHelper dbHelper = new FoodDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(FoodDatabaseHelper.TABLE_NAME, FoodDatabaseHelper.COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        Toast.makeText(this, "已刪除", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData(true, "");
    }
}
