package com.example.foodapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.foodapp.db.FoodDatabaseHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.net.Uri;

import android.content.SharedPreferences;
import android.content.Context;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private BottomNavigationView bottomNavigationView;
    private TextView tvTotalItems, tvExpiringItems;
    private MaterialButton btnLogin, btnRegister, btnAddFood, btnViewList;
    private FoodDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 設置全局異常處理器以捕獲未處理的異常
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            Log.e(TAG, "Uncaught Exception: " + throwable.getMessage(), throwable);
            runOnUiThread(() -> {
                Toast.makeText(this, "未預期錯誤: " + throwable.getMessage(), Toast.LENGTH_LONG).show();
            });
            finish();
        });

        try {
            Log.d(TAG, "Starting initialization");

            // Step 1: 載入佈局
            Log.d(TAG, "Loading layout: R.layout.activity_main");
            setContentView(R.layout.activity_main);
            Log.d(TAG, "Layout loaded successfully");

            // Step 2: 初始化工具列
            Log.d(TAG, "Initializing toolbar");
            initToolbar();
            Log.d(TAG, "Toolbar initialized successfully");

            // Step 3: 初始化資料庫
            Log.d(TAG, "Initializing database");
            initDatabase();
            Log.d(TAG, "Database initialized successfully");

            // Step 4: 初始化UI元件
            Log.d(TAG, "Initializing views");
            initViews();
            Log.d(TAG, "Views initialized successfully");

            // Step 5: 設置點擊事件
            Log.d(TAG, "Setting up click listeners");
            setupClickListeners();
            Log.d(TAG, "Click listeners set up successfully");

            // Step 6: 設置Bottom Navigation
            Log.d(TAG, "Setting up bottom navigation");
            setupBottomNavigation();
            Log.d(TAG, "Bottom navigation set up successfully");

            // Step 7: 更新統計資訊
            Log.d(TAG, "Updating statistics");
            updateStatistics();
            Log.d(TAG, "Statistics updated successfully");

        } catch (Exception e) {
            String errorMessage = "應用程式初始化失敗: " + e.getMessage();
            Log.e(TAG, errorMessage, e);
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
            finish();
        }
    }

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

    private void initDatabase() {
        try {
            dbHelper = new FoodDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase(); // 使用 getWritableDatabase 確保資料庫可寫
            if (db.isOpen()) {
                Log.d(TAG, "Database opened successfully at: " + db.getPath());
                db.close();
            } else {
                Log.e(TAG, "Database is not open");
                throw new RuntimeException("資料庫無法開啟");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing database: " + e.getMessage(), e);
            throw new RuntimeException("資料庫初始化失敗: " + e.getMessage(), e);
        }
    }

    private void initViews() {
        try {
            bottomNavigationView = findViewById(R.id.bottom_navigation);
            tvTotalItems = findViewById(R.id.tvTotalItems);
            tvExpiringItems = findViewById(R.id.tvExpiringItems);
            btnAddFood = findViewById(R.id.btnAddFood);
            btnViewList = findViewById(R.id.btnViewList);

            if (bottomNavigationView == null) {
                Log.e(TAG, "Bottom Navigation View not found in layout");
                throw new RuntimeException("Bottom Navigation View not found");
            }
            if (tvTotalItems == null) {
                Log.e(TAG, "Total Items TextView not found in layout");
                throw new RuntimeException("Total Items TextView not found");
            }
            if (tvExpiringItems == null) {
                Log.e(TAG, "Expiring Items TextView not found in layout");
                throw new RuntimeException("Expiring Items TextView not found");
            }
            if (btnLogin == null) {
                Log.w(TAG, "Login Button not found in layout");
            }
            if (btnRegister == null) {
                Log.w(TAG, "Register Button not found in layout");
            }
            if (btnAddFood == null) {
                Log.w(TAG, "Add Food Button not found in layout");
            }
            if (btnViewList == null) {
                Log.w(TAG, "View List Button not found in layout");
            }
            Log.d(TAG, "All views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views: " + e.getMessage(), e);
            throw new RuntimeException("UI元件初始化失敗: " + e.getMessage(), e);
        }
    }

    private void setupClickListeners() {
        try {
            if (btnLogin != null) {
                btnLogin.setOnClickListener(v -> {
                    // 跳轉到登入頁面，而不是在這裡處理登入邏輯
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                });
                Log.d(TAG, "Login button listener set");
            }
            if (btnRegister != null) {
                btnRegister.setOnClickListener(v -> {
                    Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                    startActivity(intent);
                });
                Log.d(TAG, "Register button listener set");
            }
            if (btnAddFood != null) {
                btnAddFood.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(MainActivity.this, AddFoodActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "Started AddFoodActivity");
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting AddFoodActivity: " + e.getMessage(), e);
                        Toast.makeText(this, "無法開啟新增食物頁面: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "Add Food button listener set");
            }
            if (btnViewList != null) {
                btnViewList.setOnClickListener(v -> {
                    try {
                        Intent intent = new Intent(MainActivity.this, FoodListActivity.class);
                        startActivity(intent);
                        Log.d(TAG, "Started FoodListActivity");
                    } catch (Exception e) {
                        Log.e(TAG, "Error starting FoodListActivity: " + e.getMessage(), e);
                        Toast.makeText(this, "無法開啟食物清單頁面: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d(TAG, "View List button listener set");
            }
            if(bottomNavigationView != null){
                bottomNavigationView.setOnItemSelectedListener(item -> {
                    int itemId = item.getItemId();

                    if (itemId == R.id.navigation_home) {
                        // 切換到 Home Fragment
                        return true;
                    } else if (itemId == R.id.navigation_profile) {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);  // 啟動 ProfileActivity
                        return true;
                    }
                    return false;
                });

            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up click listeners: " + e.getMessage(), e);
            throw new RuntimeException("點擊事件設置失敗: " + e.getMessage(), e);
        }
    }

    private void setupBottomNavigation() {
        try {
            if (bottomNavigationView == null) {
                Log.e(TAG, "BottomNavigationView is null");
                throw new RuntimeException("BottomNavigationView is null");
            }
            Log.d(TAG, "Setting up BottomNavigationView");
            bottomNavigationView.setOnItemSelectedListener(item -> {
                try {
                    int itemId = item.getItemId();
                    Log.d(TAG, "BottomNavigation item selected: " + itemId);
                    if (itemId == R.id.navigation_home) {
                        updateStatistics();
                        return true;
                    } else if (itemId == R.id.navigation_add) {
                        Intent intent = new Intent(MainActivity.this, AddFoodActivity.class);
                        startActivity(intent);
                        return true;
                    } else if (itemId == R.id.navigation_list) {
                        Intent intent = new Intent(MainActivity.this, FoodListActivity.class);
                        startActivity(intent);
                        return true;
                    } else if (itemId == R.id.navigation_recipe) {
                        Intent intent = new Intent(MainActivity.this, RecipeWebViewActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    else if (itemId == R.id.navigation_profile) {
                        Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                        startActivity(intent);  // 啟動 ProfileActivity
                        return true;
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error in bottom navigation click: " + e.getMessage(), e);
                    Toast.makeText(this, "導航出現錯誤: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return false;
            });
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
            Log.d(TAG, "BottomNavigationView setup completed");
        } catch (Exception e) {
            Log.e(TAG, "Error setting up bottom navigation: " + e.getMessage(), e);
            throw new RuntimeException("底部導航設置失敗: " + e.getMessage(), e);
        }
    }

    private void updateStatistics() {
        try {
            if (dbHelper == null) {
                Log.e(TAG, "Database helper is null");
                throw new RuntimeException("資料庫輔助對象未初始化");
            }
            Log.d(TAG, "Updating statistics");
            SQLiteDatabase db = null;
            Cursor countCursor = null;
            Cursor expiringCursor = null;

            try {
                db = dbHelper.getReadableDatabase();
                if (!db.isOpen()) {
                    Log.e(TAG, "Database is not open");
                    throw new RuntimeException("資料庫無法開啟");
                }

                // 計算總食物數量
                String countQuery = "SELECT COUNT(*) FROM " + FoodDatabaseHelper.TABLE_NAME;
                countCursor = db.rawQuery(countQuery, null);
                int totalItems = 0;
                if (countCursor != null && countCursor.moveToFirst()) {
                    totalItems = countCursor.getInt(0);
                } else {
                    Log.w(TAG, "Count cursor is null or empty");
                }

                // 計算即將過期的食物數量（3天內過期）
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 3);
                String threeDaysLater = sdf.format(calendar.getTime());

                String expiringQuery = "SELECT COUNT(*) FROM " + FoodDatabaseHelper.TABLE_NAME +
                        " WHERE " + FoodDatabaseHelper.COLUMN_EXPIRY_DATE + " <= ?";
                expiringCursor = db.rawQuery(expiringQuery, new String[]{threeDaysLater});
                int expiringItems = 0;
                if (expiringCursor != null && expiringCursor.moveToFirst()) {
                    expiringItems = expiringCursor.getInt(0);
                } else {
                    Log.w(TAG, "Expiring cursor is null or empty");
                }

                // 使用 final 變數以確保 lambda 表達式中可訪問
                final int finalTotalItems = totalItems;
                final int finalExpiringItems = expiringItems;

                // 更新UI
                runOnUiThread(() -> {
                    if (tvTotalItems != null) {
                        tvTotalItems.setText(String.valueOf(finalTotalItems));
                        Log.d(TAG, "Updated total items: " + finalTotalItems);
                    } else {
                        Log.w(TAG, "tvTotalItems is null");
                    }
                    if (tvExpiringItems != null) {
                        tvExpiringItems.setText(String.valueOf(finalExpiringItems));
                        Log.d(TAG, "Updated expiring items: " + finalExpiringItems);
                    } else {
                        Log.w(TAG, "tvExpiringItems is null");
                    }
                });

            } finally {
                if (countCursor != null) {
                    countCursor.close();
                }
                if (expiringCursor != null) {
                    expiringCursor.close();
                }
                if (db != null && db.isOpen()) {
                    db.close();
                }
                Log.d(TAG, "Database resources closed");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error updating statistics: " + e.getMessage(), e);
            runOnUiThread(() -> {
                if (tvTotalItems != null) {
                    tvTotalItems.setText("0");
                }
                if (tvExpiringItems != null) {
                    tvExpiringItems.setText("0");
                }
            });
            throw new RuntimeException("統計資訊更新失敗: " + e.getMessage(), e);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            Log.d(TAG, "onResume: Updating statistics");
            updateStatistics();
            if (bottomNavigationView != null) {
                bottomNavigationView.setSelectedItemId(R.id.navigation_home);
                Log.d(TAG, "onResume: BottomNavigation set to home");
            } else {
                Log.w(TAG, "onResume: BottomNavigationView is null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in onResume: " + e.getMessage(), e);
            Toast.makeText(this, "onResume錯誤: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (dbHelper != null) {
                dbHelper.close();
                Log.d(TAG, "Database helper closed");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error closing database helper: " + e.getMessage(), e);
        }
    }
}