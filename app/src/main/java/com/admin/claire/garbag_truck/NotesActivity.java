package com.admin.claire.garbag_truck;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;


public class NotesActivity extends AppCompatActivity {
    private EditText editTitle;
    private EditText editContent;
    private Button btnOK, btnCancel;
    private ImageView imgAlarm, imgTakePhoto, imgChoosePohto, imgSelectColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        initView();
        initHandler();
        // 取得Intent物件
        Intent intent = getIntent();
        //讀取Action名稱
        String action = intent.getAction();

        // 如果是修改記事
        if (action.equals("com.admin.claire.garbag_truck.EDIT_ITEM")) {
            //接受與記事標題
            String titleText = intent.getStringExtra("titleText");
            editTitle.setText(titleText);
        }

    }

    private void initView() {
        editTitle = (EditText)findViewById(R.id.edit_title);
        editContent = (EditText)findViewById(R.id.edit_Content);
        btnOK = (Button)findViewById(R.id.btn_Ok);
        btnCancel = (Button)findViewById(R.id.btn_Cancel);
        imgAlarm = (ImageView)findViewById(R.id.imageAlarm);
        imgTakePhoto = (ImageView)findViewById(R.id.imageTakePhoto);
        imgChoosePohto = (ImageView)findViewById(R.id.imageChoosePhoto);
        imgSelectColor = (ImageView)findViewById(R.id.imageSelectColor);
    }


    private void initHandler() {
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId()== R.id.btn_Ok){
                    // 讀取使用者輸入的標題與內容
                    String titleText = editTitle.getText().toString();
                    String contentText = editContent.getText().toString();

                    // 取得回傳資料用的Intent物件
                    Intent result = getIntent();
                    // 設定標題與內容
                    result.putExtra("titleText", titleText);
                    result.putExtra("contentText", contentText);

                    // 設定回應結果為確定
                    setResult(Activity.RESULT_OK, result);
                }

                finish();
            }
        } );

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


}
