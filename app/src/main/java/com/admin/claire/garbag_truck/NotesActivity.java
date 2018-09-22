package com.admin.claire.garbag_truck;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.admin.claire.garbag_truck.MainActivity.mTracker;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREFS_NAME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_DARK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PINK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PURPLE_THEME;


public class NotesActivity extends AppCompatActivity {
    private EditText editTitle;
    private EditText editContent;
    private ImageView btnOK, btnCancel;
    private ImageView imgAlarm, imgTakePhoto, imgSelectColor;
    private TextView alarmTimeTxt;

    // 檔案名稱
    private String fileName;
    // 照片
    private ImageView picture;
    // 寫入外部儲存設備授權請求代碼
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION = 100;

    // 啟動功能用的請求代碼
    private static final int START_CAMERA = 0;
    private static final int START_ALARM = 1;
    private static final int START_COLOR = 2;

    // 記事物件
    NotesItem notesItem;

    // 增加廣告
    private AdView mAdView;

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
        setContentView(R.layout.activity_notes);

        // 增加廣告
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        initView();
        initHandler();

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

            if (notesItem.getAlarmDatetime() != 0){
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm EEE");
                String str = df.format(notesItem.getAlarmDatetime());
                //Log.e("ALARMEDIT", "onTimeSet: " + str );
                alarmTimeTxt.setText("提醒:" + str);
                alarmTimeTxt.getText();
            }
        }
        // 新增記事
        else {
            notesItem = new NotesItem();
            alarmTimeTxt.setText("");
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode){
                case START_CAMERA:
                    // 設定照片檔案名稱
                    notesItem.setFileName(fileName);
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

    @Override
    protected void onResume() {
        super.onResume();

        // 如果有檔案名稱
        if (notesItem.getFileName() != null && notesItem.getFileName().length() > 0) {
            // 照片檔案物件
            File file = configFileName("P", ".jpg");

            // 如果照片檔案存在
            if (file.exists()) {
                // 顯示照片元件
                picture.setVisibility(View.VISIBLE);
                // 設定照片
                FileUtil.fileToImageView(file.getAbsolutePath(), picture);
            }
        }
    }


    private void initView() {
        editTitle = (EditText)findViewById(R.id.edit_title);
        editContent = (EditText)findViewById(R.id.edit_Content);
        btnOK = (ImageView)findViewById(R.id.btn_Ok);
        btnCancel = (ImageView)findViewById(R.id.btn_Cancel);
        alarmTimeTxt = (TextView)findViewById(R.id.alarmDateTime);

        imgAlarm = (ImageView)findViewById(R.id.imageAlarm);
        imgTakePhoto = (ImageView)findViewById(R.id.imageTakePhoto);
        imgSelectColor = (ImageView)findViewById(R.id.imageSelectColor);
        // 取得顯示照片的ImageView元件
        picture = (ImageView) findViewById(R.id.picture);

        imgTakePhoto.setOnClickListener(imgTakePhotoListener); //拍照註冊
        imgSelectColor.setOnClickListener(imgSelectColorListener); //設定顏色註冊
        imgAlarm.setOnClickListener(imgAlarmListener); //提醒功能註冊

    }

    public static Colors getColors(int color) {
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
                        // 建立SharedPreferences物件
                        SharedPreferences sharedPreferences =
                                PreferenceManager.getDefaultSharedPreferences(NotesActivity.this);
                        // 讀取設定的預設顏色
                        int color = sharedPreferences.getInt("DEFAULT_COLOR", -1);
                        notesItem.setColor(getColors(color));

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

    // 設定提醒日期時間
    private void processSetAlarm() {
        Calendar calendar = Calendar.getInstance();

        if (notesItem.getAlarmDatetime() != 0) {
            // 設定為已經儲存的提醒日期時間
            calendar.setTimeInMillis(notesItem.getAlarmDatetime());

        }

        // 讀取年、月、日、時、分
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);
        final int hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int minute = calendar.get(Calendar.MINUTE);

        // 儲存設定的提醒日期時間
        final Calendar alarm = Calendar.getInstance();

        // 設定提醒時間
        TimePickerDialog.OnTimeSetListener timeSetListener =
                new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                alarm.set(Calendar.HOUR_OF_DAY, hourOfDay);
                alarm.set(Calendar.MINUTE, minute);
                notesItem.setAlarmDatetime(alarm.getTimeInMillis());

                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm EEE");
                String str = df.format(alarm.getTimeInMillis());
                //Log.e("ALARM", "onTimeSet: " + str );
                alarmTimeTxt.setText("設定提醒:" + str);
            }

        };
        // 選擇時間對話框
        final TimePickerDialog tpd  = new TimePickerDialog(
                this, timeSetListener, hour, minute, true);

        // 設定提醒日期
        DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                alarm.set(Calendar.YEAR, year);
                alarm.set(Calendar.MONTH, month);
                alarm.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //繼續選擇提醒時間
                tpd.show();

            }
        };
        // 建立與顯示選擇日期對話框
        final DatePickerDialog dpd = new DatePickerDialog(
                this, dateSetListener, year, month, day);
        dpd.show();
    }

    //提醒功能
    private View.OnClickListener imgAlarmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GoogleAnalyticsButtonClick
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("AlarmNotesAction")
                    .setAction("NotesAlarm")
                    .build());
            v.startAnimation(AnimationUtils.loadAnimation(NotesActivity.this,R.anim.click_animation));
            // 設定提醒日期時間
            processSetAlarm();
        }
    };

    //設定顏色
    private View.OnClickListener imgSelectColorListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GoogleAnalyticsButtonClick
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("ColorNotesAction")
                    .setAction("NotesColor")
                    .build());
            v.startAnimation(AnimationUtils.loadAnimation(NotesActivity.this,R.anim.click_animation));

            //啟動設定顏色的Activity元件
            startActivityForResult(new Intent(NotesActivity.this,
                    ColorActivity.class),START_COLOR);

        }
    };

    // 拍照
    private View.OnClickListener imgTakePhotoListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GoogleAnalyticsButtonClick
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("TakePhotoAction")
                    .setAction("TakePhoto")
                    .build());
            v.startAnimation(AnimationUtils.loadAnimation(
                    NotesActivity.this,R.anim.click_animation));

            requestStoragePermission();
        }
    };

    // 拍攝照片
    private void takePicture() {
        // 啟動相機元件用的Intent物件
        Intent intentCamera =
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 照片檔案名稱
        File pictureFile = configFileName("P", ".jpg");
        //Uri uri = Uri.fromFile(pictureFile);

        // 如果裝置版本是7.0（包含）以上 判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri uri = FileProvider.getUriForFile(NotesActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",
                    pictureFile);
            // 設定檔案名稱
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            intentCamera.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            Uri uri = Uri.fromFile(pictureFile);
            // 設定檔案名稱
            intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }


        // 啟動相機元件
        startActivityForResult(intentCamera, START_CAMERA);
    }

    private File configFileName(String prefix , String extension) {
        // 如果記事資料已經有檔案名稱
        if (notesItem.getFileName() != null && notesItem.getFileName().length() > 0){
            fileName = notesItem.getFileName();
        }
        // 產生檔案名稱
        else {
            fileName = FileUtil.getUniqueFileName();
        }
        // 建立並傳回外部儲存媒體參數指定的路徑
        return new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                prefix + fileName + extension);
    }

    // 讀取與處理寫入外部儲存設備授權請求
    private void requestStoragePermission() {
        // 如果裝置版本是6.0（包含）以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 取得授權狀態，參數是請求授權的名稱
            int hasPermission = checkSelfPermission(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

            // 如果未授權
            if (hasPermission != PackageManager.PERMISSION_GRANTED) {
                // 請求授權
                //     第一個參數是請求授權的名稱
                //     第二個參數是請求代碼
                requestPermissions(
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION);
                return;
            }
        }

        // 如果裝置版本是6.0以下，
        // 或是裝置版本是6.0（包含）以上，使用者已經授權，
        // 拍攝照片
        takePicture();
    }

//覆寫請求授權後執行的方法
//     第一個參數是請求代碼
//     第二個參數是請求授權的名稱
//     第三個參數是請求授權的結果，PERMISSION_GRANTED或PERMISSION_DENIED

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        // 如果是寫入外部儲存設備授權請求
        if (requestCode == REQUEST_WRITE_EXTERNAL_STORAGE_PERMISSION) {
            // 如果在授權請求選擇「允許」
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                // 拍攝照片
                takePicture();
            }
            // 如果在授權請求選擇「拒絕」
            else {
                // 顯示沒有授權的訊息
                Toast.makeText(this, R.string.write_external_storage_denied,
                        Toast.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    // 點擊畫面右下角的照片縮圖元件
    public void clickPicture(View view) {
        Intent intent = new Intent(this, PictureActivity.class);

        // 設定圖片檔案名稱
        intent.putExtra("pictureName", configFileName("P", ".jpg").getAbsolutePath());

        // 如果裝置的版本是LOLLIPOP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptions options = ActivityOptions
                    .makeSceneTransitionAnimation(this, picture, "picture");
            startActivity(intent, options.toBundle());
        }
        else {
            startActivity(intent);
        }
    }

}
