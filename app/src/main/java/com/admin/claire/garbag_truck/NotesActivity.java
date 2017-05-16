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

import java.io.File;
import java.util.Calendar;
import java.util.Date;


public class NotesActivity extends AppCompatActivity {
    private EditText editTitle;
    private EditText editContent;
    private Button btnOK, btnCancel;
    private ImageView imgAlarm, imgTakePhoto, imgChoosePohto, imgSelectColor;

    // 啟動功能用的請求代碼
    private static final int START_CAMERA = 0;
    private static final int START_ALARM = 1;
    private static final int START_COLOR = 2;

    // 記事物件
    NotesItem notesItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        initView();
        initHandler();
        initItemHandler();

        // 取得Intent物件
        Intent intent = getIntent();
        //讀取Action名稱
        String action = intent.getAction();

        // 如果是修改記事
        if (action.equals("com.admin.claire.garbag_truck.EDIT_ITEM")) {
            // 接收記事物件與設定標題、內容
            notesItem = (NotesItem)intent.getExtras().
                    getSerializable("com.admin.claire.garbag_truck.ITEM");

            editTitle.setText(notesItem.getTitle());
            editContent.setText(notesItem.getContent());
        }
        // 新增記事
        else {
            notesItem = new NotesItem();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case START_CAMERA:
                    break;
                case  START_ALARM:
                    break;
                // 設定顏色
                case  START_COLOR:
                    int colorId = data.getIntExtra("colorId", Colors.BLUE.parseColor());
                    notesItem.setColor(getColors(colorId));
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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

    private void initItemHandler() {
        imgSelectColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //啟動設定顏色的Activity元件
                startActivityForResult(new Intent(NotesActivity.this,
                        ColorActivity.class),START_COLOR);
            }
        });
    }

    private void initHandler() {
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId()== R.id.btn_Ok){
                    // 讀取使用者輸入的標題與內容
                    String titleText = editTitle.getText().toString();
                    String contentText = editContent.getText().toString();

                    // 設定記事物件的標題與內容
                    notesItem.setTitle(titleText);
                    notesItem.setContent(contentText);

                    // 如果是修改記事
                    if (getIntent().getAction().equals(
                            "com.admin.claire.garbag_truck.EDIT_ITEM")) {

                        notesItem.setLastModify(new Date().getTime());
                    }
                    // 新增記事
                    else {
                        notesItem.setDatetime(new Date().getTime());
                    }

                    // 取得回傳資料用的Intent物件
                    Intent result = getIntent();
                    // 設定回傳的記事物件
                    result.putExtra("com.admin.claire.garbag_truck.ITEM", notesItem);
                    setResult(Activity.RESULT_OK, result);
                }
                // 結束
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


    private Colors getColors(int color) {
        Colors result = Colors.BLUE;

        if (color == Colors.LIGHTGRAY.parseColor()) {
            result = Colors.LIGHTGRAY;
        }
        else if (color == Colors.ORANGE.parseColor()) {
            result = Colors.ORANGE;
        }
        else if (color == Colors.PURPLE.parseColor()) {
            result = Colors.PURPLE;
        }
        else if (color == Colors.RED.parseColor()) {
            result = Colors.RED;
        }
        else if (color == Colors.GREEN.parseColor()) {
            result = Colors.GREEN;
        }
        return result;
    }
}
