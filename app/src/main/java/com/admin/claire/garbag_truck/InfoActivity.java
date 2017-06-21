package com.admin.claire.garbag_truck;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREFS_NAME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_DARK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PINK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PURPLE_THEME;

public class InfoActivity extends AppCompatActivity {

    private Button btnOk;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private static final String[]data = {
            "週日、三：\n停收垃圾及資源回收物(廚餘)",
            "週一、五：\n平面類：\n" +
                    "紙類\n" +
                    "舊衣類\n" +
                    "乾淨塑膠袋",
            "週二、四、六：\n立體類︰\n" +
                    "乾淨保麗龍\n" +
                    "一般類（瓶罐、容器、小家電等）"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        initView();
        initHandler();

    }

    private void initView() {
        btnOk = (Button)findViewById(R.id.btn_Info);
        listView = (ListView)findViewById(R.id.listInfo);
        adapter = new ArrayAdapter<String>(this, R.layout.list_info,
                R.id.text_Note, data);

        listView.setAdapter(adapter);
    }

    private void initHandler() {
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
