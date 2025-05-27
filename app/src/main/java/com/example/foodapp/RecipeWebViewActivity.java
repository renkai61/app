package com.example.foodapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.foodapp.db.FoodDatabaseHelper;
import com.google.android.flexbox.FlexboxLayout;


public class RecipeWebViewActivity extends AppCompatActivity {
    WebView webView;
    FlexboxLayout foodShortcutContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_webview);

        SearchView searchView = findViewById(R.id.searchView);
        webView = findViewById(R.id.webView);
        foodShortcutContainer = findViewById(R.id.foodShortcutContainer);

        // WebView 設定
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(RecipeWebViewActivity.this, "載入失敗: " + description, Toast.LENGTH_SHORT).show();
            }
        });

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);
        webView.clearCache(true);
        webView.clearHistory();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String encodedQuery = Uri.encode(query);
                String searchUrl = "https://icook.tw/search/" + encodedQuery;
                webView.loadUrl(searchUrl);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // ✅ 呼叫產生按鈕
        createFoodShortcutButtons(searchView);
    }


    private void createFoodShortcutButtons(SearchView searchView) {
        FoodDatabaseHelper dbHelper = new FoodDatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                true,
                FoodDatabaseHelper.TABLE_NAME,
                new String[]{FoodDatabaseHelper.COLUMN_NAME},
                null, null, null, null, null, null
        );

        while (cursor.moveToNext()) {
            String foodName = cursor.getString(cursor.getColumnIndexOrThrow(FoodDatabaseHelper.COLUMN_NAME));
            Button btn = new Button(this);
            btn.setText(foodName);
            btn.setAllCaps(false);
            btn.setBackgroundColor(Color.parseColor("#EEEEEE"));
            btn.setPadding(20, 10, 20, 10);


            FlexboxLayout.LayoutParams params = new FlexboxLayout.LayoutParams(
                    FlexboxLayout.LayoutParams.WRAP_CONTENT,
                    FlexboxLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                String encoded = Uri.encode(foodName);
                String url = "https://icook.tw/search/" + encoded;
                webView.loadUrl(url);
                searchView.setQuery(foodName, false);
            });

            foodShortcutContainer.addView(btn);
        }

        cursor.close();
        db.close();
    }
}