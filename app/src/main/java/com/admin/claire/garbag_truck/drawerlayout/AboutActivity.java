package com.admin.claire.garbag_truck.drawerlayout;

import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.admin.claire.garbag_truck.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
