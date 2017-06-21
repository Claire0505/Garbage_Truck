package com.admin.claire.garbag_truck.drawerlayout;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.admin.claire.garbag_truck.R;

import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREFS_NAME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_DARK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PINK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PURPLE_THEME;

public class AboutActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_about);

        //啟用<- up按鈕
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        TextView openDataUrl = (TextView)findViewById(R.id.textTaipei);
        openDataUrl.setLinkTextColor(Color.parseColor("#000000"));
        openDataUrl.setMovementMethod(LinkMovementMethod.getInstance());

        TextView recycleUrl = (TextView)findViewById(R.id.textRecycle);
        recycleUrl.setLinkTextColor(Color.parseColor("#000000"));
        recycleUrl.setMovementMethod(LinkMovementMethod.getInstance());

        TextView yahooUrl = (TextView)findViewById(R.id.textYahoo);
        yahooUrl.setLinkTextColor(Color.parseColor("#000000"));
        yahooUrl.setMovementMethod(LinkMovementMethod.getInstance());
    }
}
