package com.admin.claire.garbag_truck.preference;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.admin.claire.garbag_truck.MainActivity;
import com.admin.claire.garbag_truck.R;

public class ThemeToggle extends AppCompatActivity {

    public static final String PREFS_NAME = "prefs";
    public static final String PREF_DARK_THEME = "dark_theme";
    public static final String PREF_PINK_THEME = "pink_theme";
    public static final String PREF_PURPLE_THEME = "purple_theme";
    private Switch darkTheme, pinkTheme, purpleTheme;

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
        setContentView(R.layout.activity_theme_toggle);

        //啟用<- up按鈕
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        darkTheme = (Switch)findViewById(R.id.switchDark);
        darkTheme.setChecked(useDarkTheme);

        pinkTheme = (Switch)findViewById(R.id.switchPink);
        pinkTheme.setChecked(usePinkTheme);

        purpleTheme = (Switch)findViewById(R.id.switchPurple);
        purpleTheme.setChecked(usePurpleTheme);
        initHandler();
    }

    private void initHandler() {
        darkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                toggleDarkTheme(isChecked);
                pinkTheme.setChecked(false);
                purpleTheme.setChecked(false);
            }
        });

        pinkTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                togglePinkTheme(isChecked);
                darkTheme.setChecked(false);
                purpleTheme.setChecked(false);
            }
        });

        purpleTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                togglePurpleTheme(isChecked);
                darkTheme.setChecked(false);
                pinkTheme.setChecked(false);
            }
        });
    }

    private void toggleDarkTheme(boolean darkTheme) {
        //來存儲用戶的首選項並重新啟動當前活動
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_DARK_THEME, darkTheme);
        editor.apply();
//        Intent intent = getIntent();
//        finish();
//        startActivity(intent);
        startActivity(new Intent(ThemeToggle.this,MainActivity.class));
    }

    private void togglePinkTheme(boolean pinkTheme) {
        //來存儲用戶的首選項並重新啟動當前活動
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_PINK_THEME, pinkTheme);
        editor.apply();

        startActivity(new Intent(ThemeToggle.this,MainActivity.class));
    }

    private void togglePurpleTheme(boolean pinkTheme) {
        //來存儲用戶的首選項並重新啟動當前活動
        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit();
        editor.putBoolean(PREF_PURPLE_THEME, pinkTheme);
        editor.apply();

        startActivity(new Intent(ThemeToggle.this,MainActivity.class));
    }
}
