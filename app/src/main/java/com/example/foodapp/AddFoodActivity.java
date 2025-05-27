package com.example.foodapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;        //繼承這個
import com.example.foodapp.db.FoodDatabaseHelper;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import android.app.DatePickerDialog;
import java.util.Calendar;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;
import androidx.core.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.Manifest;


public class AddFoodActivity extends AppCompatActivity {
    EditText edtName, edtType, edtQuantity, edtExpiry;      //UI 組件宣告
    Button btnSave;

    private ImageView imgFoodPhoto;
    private Button btnPickPhoto;
    private Uri selectedImageUri = null;


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 權限允許後，自動觸發選照片按鈕點擊（或用其他方式讓用戶再點）
                btnPickPhoto.performClick();
            } else {
                Toast.makeText(this, "需要相簿存取權限才能選擇照片", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_food);

        edtName = findViewById(R.id.edtName);
        edtType = findViewById(R.id.edtType);
        edtQuantity = findViewById(R.id.edtQuantity);
        edtExpiry = findViewById(R.id.edtExpiry);
        btnSave = findViewById(R.id.btnSave);

        //這行我感覺沒差，防呆，針對 Android 13 (API level 33) 以上版本請求通知權限
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        edtExpiry.setFocusable(false);  // 禁止手動輸入

        //這部分是日期的
        edtExpiry.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);       // Calendar 的月份從 0 開始計算
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    AddFoodActivity.this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String dateString = String.format(Locale.getDefault(), "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        edtExpiry.setText(dateString);
                    },
                    year, month, day
            );

            datePickerDialog.show();
        });

        imgFoodPhoto = findViewById(R.id.imgFoodPhoto);
        btnPickPhoto = findViewById(R.id.btnPickPhoto);

// 啟動系統相簿選照片
        ActivityResultLauncher<Intent> pickPhotoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        selectedImageUri = result.getData().getData();
                        imgFoodPhoto.setImageURI(selectedImageUri);
                    }
                }
        );

        btnPickPhoto.setOnClickListener(v -> {
            // 先檢查權限，Android 13+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 100);
                    return;
                }
            } else {
                // Android 12 以下
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                    return;
                }
            }

            // 打開相簿
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickPhotoLauncher.launch(intent);
        });

        //資料庫操作
        btnSave.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String type = edtType.getText().toString();
            int quantity = Integer.parseInt(edtQuantity.getText().toString());
            String expiry = edtExpiry.getText().toString();

            if (name.trim().isEmpty() || type.trim().isEmpty() || edtQuantity.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "請填寫所有必填欄位", Toast.LENGTH_SHORT).show();
                return;
            }
            FoodDatabaseHelper dbHelper = new FoodDatabaseHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(FoodDatabaseHelper.COLUMN_NAME, name);
            values.put(FoodDatabaseHelper.COLUMN_TYPE, type);
            values.put(FoodDatabaseHelper.COLUMN_QUANTITY, quantity);
            values.put(FoodDatabaseHelper.COLUMN_EXPIRY_DATE, expiry);

            if (selectedImageUri != null) {
                values.put(FoodDatabaseHelper.COLUMN_PHOTO_URI, selectedImageUri.toString());
            } else {
                values.put(FoodDatabaseHelper.COLUMN_PHOTO_URI, "");
            }

            long newRowId = db.insert(FoodDatabaseHelper.TABLE_NAME, null, values);
            //提醒
            if (newRowId != -1) {
                Toast.makeText(this, "新增成功", Toast.LENGTH_SHORT).show();
                try {
                    // 解析使用者選擇的過期日期
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Calendar expiryDate = Calendar.getInstance();
                    expiryDate.setTime(sdf.parse(expiry)); // 設定為實際過期日期

                    // 提前一天提醒 (過期前一天)
                    expiryDate.add(Calendar.DAY_OF_MONTH, -1);

                    // 設定提醒時間為當天的 09:00 (可自訂時間)
                    expiryDate.set(Calendar.HOUR_OF_DAY, 9);
                    expiryDate.set(Calendar.MINUTE, 0);
                    expiryDate.set(Calendar.SECOND, 0);
                    expiryDate.set(Calendar.MILLISECOND, 0);

                    // 檢查提醒時間是否已經過了
                    Calendar now = Calendar.getInstance();
                    if (expiryDate.getTimeInMillis() <= now.getTimeInMillis()) {
                        // 如果提醒時間已過，就不設定提醒，或者可以立即顯示提醒
                        Toast.makeText(this, "食品即將到期，請注意！", Toast.LENGTH_LONG).show();
                    } else {
                        Intent notifyIntent = new Intent(this, ExpiryReceiver.class);
                        notifyIntent.putExtra("foodName", name);
                        notifyIntent.putExtra("expiryDate", expiry); // 傳遞實際過期日期

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                this, (int) newRowId, notifyIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );

                        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, expiryDate.getTimeInMillis(), pendingIntent);

                        // 顯示提醒設定成功的訊息
                        String reminderInfo = String.format("已設定提醒：%s 09:00",
                                sdf.format(expiryDate.getTime()));
                        Toast.makeText(this, reminderInfo, Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(this, "設定提醒失敗", Toast.LENGTH_SHORT).show();
                }
                finish();
            } else {
                Toast.makeText(this, "新增失敗", Toast.LENGTH_SHORT).show();
            }

            db.close();
        });

    }
}