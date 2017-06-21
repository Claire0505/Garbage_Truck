package com.admin.claire.garbag_truck;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREFS_NAME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_DARK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PINK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PURPLE_THEME;

/**
 * Created by claire on 2017/5/18.
 */

public class PictureActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Use the chosen theme
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        boolean useDarkTheme = preferences.getBoolean(PREF_DARK_THEME, false);
        boolean usePinkTheme = preferences.getBoolean(PREF_PINK_THEME, false);
        boolean usePurpleTheme = preferences.getBoolean(PREF_PURPLE_THEME, false);
        if (useDarkTheme){
            setTheme(R.style.CustomerTheme_Black);
        }else if (usePinkTheme){
            setTheme(R.style.CustomerTheme_Pink);
        }else if (usePurpleTheme) {
            setTheme(R.style.CustomerTheme_Purple);
        }

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_picture);

        // 取得照片元件
        ImageView picture_view = (ImageView) findViewById(R.id.picture_view);

        // 讀取照片檔案名稱
        Intent intent = getIntent();
        String pictureName = intent.getStringExtra("pictureName");

        if (pictureName != null) {
            // 設定照片元件
            FileUtil.fileToImageView(pictureName, picture_view);
        }
    }

    public void clickPicture(View view) {
        // 如果裝置的版本是LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAfterTransition();
        }
        else {
            finish();
        }
    }
}

