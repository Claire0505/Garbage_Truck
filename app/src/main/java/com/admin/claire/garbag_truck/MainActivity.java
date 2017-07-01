package com.admin.claire.garbag_truck;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.admin.claire.garbag_truck.broadcast.AlarmReceiver;
import com.admin.claire.garbag_truck.database.NotesItemDAO;
import com.admin.claire.garbag_truck.drawerlayout.AboutActivity;
import com.admin.claire.garbag_truck.drawerlayout.DirectionsActivity;
import com.admin.claire.garbag_truck.preference.PrefActivity;
import com.admin.claire.garbag_truck.preference.ThemeToggle;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.R.attr.name;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREFS_NAME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_DARK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PINK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PURPLE_THEME;

public class MainActivity extends AppCompatActivity {

    private LinearLayout tag_Layout,settings_Layout, about_Layout, direction_Layout;
    private Snackbar snackbar;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mActionBarDrawerToggle;
    private ListView mListNote;
    private ImageView mGarbageImg;
    private ImageView mLocationImg;
    private ImageView mNoteImg;
    private ImageView mInfoImg;

    private static String weatherUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20item.condition%20from%20weather.forecast%20where%20woeid%20%3D%2055812231%20and%20u%3D%22c%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    private String TAG = "weatherJSON";

    private TextView weather;
    private TextView weather_temp;
    private ImageView icon_img;
    private String code;
    private String temp;
    private String text;

    // ListView使用的自定Adapter物件
    private NotesItemAdapter notesItemAdapter;
    // 儲存所有記事本的List物件
    private List<NotesItem> notesItems;
    // 選單項目物件
    private MenuItem revert_item, delete_item;
    // 已選擇項目數量
    private int selectedCount = 0;

    // 宣告資料庫功能類別欄位變數
    private NotesItemDAO notesItemDAO;

    public static GoogleAnalytics analytics;
    public static Tracker mTracker;

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
        setContentView(R.layout.activity_main);
        //Log.e(TAG, "onCreate: " );

        getData();
        initView();
        initLayout();
        //GoogleAnalytics
        analytics = GoogleAnalytics.getInstance(this);
        mTracker = analytics.newTracker("UA-101542959-1");

        // 建立資料庫物件
        notesItemDAO = new NotesItemDAO(getApplicationContext());

        // 如果資料庫是空的，就建立一些範例資料
        // 這是為了方便測試用的，完成應用程式以後可以拿掉
        if (notesItemDAO.getCount() == 0) {
            notesItemDAO.sample();
        }

        // 取得所有記事資料
        notesItems = notesItemDAO.getAll();
        notesItemAdapter = new NotesItemAdapter(this, R.layout.singleitem, notesItems);
        mListNote.setAdapter(notesItemAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();

        // 取得所有記事資料
        notesItems = notesItemDAO.getAll();
        notesItemAdapter = new NotesItemAdapter(this, R.layout.singleitem, notesItems);
        mListNote.setAdapter(notesItemAdapter);
        //Log.e(TAG, "onResume: " );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // 如果被啟動的Activity元件,傳回確定的結果
        if (resultCode == Activity.RESULT_OK) {
            // 讀取記事物件
            NotesItem notesItem = (NotesItem)data.getExtras().
                    getSerializable("com.admin.claire.garbag_truck.ITEM");

            // 是否修改提醒設定
            boolean updateAlarm = false;

            // 如果是新增記事
            if (requestCode == 0){
                // 新增記事資料到資料庫
                notesItem = notesItemDAO.insert(notesItem);

                // 加入新增的記事物件
                notesItems.add(notesItem);

                // 通知資料已經改變，ListView元件才會重新顯示(ListView使用的自定Adapter物件)
                notesItemAdapter.notifyDataSetChanged();

                updateAlarm = true;
            }
             //如果是修改記事
            else if (requestCode == 1){
                // 讀取記事編號
                int position = data.getIntExtra("position", -1);

                if (position != -1) {
                    // 讀取原來的提醒設定
                   NotesItem ori = notesItemDAO.get(notesItem.getId());
                    // 判斷是否需要設定提醒
                    updateAlarm = (notesItem.getAlarmDatetime() != ori.getAlarmDatetime());

                    // 修改資料庫中的記事資料
                    notesItemDAO.update(notesItem);
                    notesItems.set(position, notesItem);

                    // 通知資料已經改變，ListView元件才會重新顯示
                    notesItemAdapter.notifyDataSetChanged();
                }

            }

            // 設定提醒
            if (notesItem.getAlarmDatetime() != 0 && updateAlarm) {
                Intent intent = new Intent(this, AlarmReceiver.class);

                // 加入記事編號
                intent.putExtra("id", notesItem.getId());

                PendingIntent pi = PendingIntent.getBroadcast(
                        this, (int)notesItem.getId(),
                        intent, PendingIntent.FLAG_ONE_SHOT);

                AlarmManager am = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
                am.set(AlarmManager.RTC_WAKEUP, notesItem.getAlarmDatetime(), pi);
            }

        }

    }

    private AdapterView.OnItemClickListener itemListener =
            new AdapterView.OnItemClickListener() {
                // 第一個參數是使用者操作的ListView物件
                // 第二個參數是使用者選擇的項目
                // 第三個參數是使用者選擇的項目編號，第一個是0
                // 第四個參數在這裡沒有用途
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //GoogleAnalyticsButtonClick
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("NotesEditAction")
                    .setAction("NotesEdit")
                    .build());

            // 讀取選擇的記事物件
            NotesItem notesItem = notesItemAdapter.getItem(position);
            // 使用Action名稱建立啟動另一個Activity元件需要的Intent物件

            // 如果已經有勾選的項目
            if (selectedCount > 0) {
                // 處理是否顯示已選擇項目
                processMenu(notesItem);
                // 重新設定記事項目
                notesItemAdapter.set(position, notesItem);
            }
            else {

                Intent intent = new Intent("com.admin.claire.garbag_truck.EDIT_ITEM");

                // 設定記事編號與標題
                intent.putExtra("position", position);
                intent.putExtra("com.admin.claire.garbag_truck.ITEM", notesItem);

                // 呼叫「startActivityForResult」，第二個參數「1」表示執行修改
                startActivityForResult(intent, 1);
            }

        }


    };

    private AdapterView.OnItemLongClickListener itemLongListener =
            new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view,
                                       int position, long id) {

            //GoogleAnalyticsButtonClick
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("DeleteNotesAction")
                    .setAction("NotesDelete")
                    .build());
            // 讀取選擇的記事物件
            NotesItem notesItem = notesItemAdapter.getItem(position);
            // 處理是否顯示已選擇項目
            processMenu(notesItem);
            // 重新設定記事項目
            notesItemAdapter.set(position, notesItem);
            return true;
        }
    };

    // 處理是否顯示已選擇項目
    private void processMenu(NotesItem notesItem) {
        // 如果需要設定記事項目
        if (notesItem != null) {
            // 設定已勾選的狀態
            notesItem.setSelected(!notesItem.isSelected());

            // 計算已勾選數量
            if (notesItem.isSelected()) {
                selectedCount++;
            }
            else {
                selectedCount--;
            }
        }
        // 根據選擇的狀況，設定是否顯示選單項目
       // search_item.setVisible(selectedCount == 0);
        revert_item.setVisible(selectedCount > 0);
        delete_item.setVisible(selectedCount > 0);
    }

    //各別列表明細
    private View.OnClickListener mGarbageImgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GoogleAnalyticsButtonClick
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("GarbageTruckAction")
                    .setAction("GarbageListDetail")
                    .build());
           //檢查網路連線
            netWork(v);
            //onClick時的動畫旋轉效果
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.click_animation));
            startActivity(new Intent(MainActivity.this, ListViewActivity.class));
        }
    };

   //地圖定位所有明細
    private View.OnClickListener mLocationImgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           // netWork(v);
            //GoogleAnalyticsButtonClick
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("MapAction")
                    .setAction("MapLocation")
                    .build());
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.click_animation));
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        }
    };

    //新增記事
    private View.OnClickListener mNoteImgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GoogleAnalyticsButtonClick
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("NotesAction")
                    .setAction("Notes")
                    .build());
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.click_animation));
            // 使用Action名稱建立啟動另一個Activity元件需要的Intent物件
            Intent intent = new Intent("com.admin.claire.garbag_truck.ADD_ITEM");
            // 呼叫「startActivityForResult」，，第二個參數「0」表示執行新增
            startActivityForResult(intent, 0);
        }
    };

    private View.OnClickListener mInfoImgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //GoogleAnalyticsButtonClick
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("InfoAction")
                    .setAction("Info")
                    .build());
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.click_animation));
            startActivity(new Intent(MainActivity.this, InfoActivity.class));

        }
    };

    private void getData(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                weatherUrl,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        parserWeatherJson(response);
                        weather.setText(text);
                        weather_temp.setText(temp);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString() );
                        Toast.makeText(MainActivity.this,error.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    private void parserWeatherJson(JSONObject jsonObject){

        try {
            JSONObject Object = new JSONObject();

            JSONObject query = jsonObject.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");
            JSONObject channel = results.getJSONObject("channel");
            JSONObject item =channel.getJSONObject("item");
            JSONObject condition = item.getJSONObject("condition");

            code = condition.getString("code");
            temp = condition.getString("temp");
            text = condition.getString("text");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch(code){
                        case "45":
                        case "47":
                        case "3":
                        case "4": //雷陣雨
                            icon_img.setImageResource(R.drawable.thunderstorms);
                            break;
                        case "8":
                        case "9":
                        case "10":
                        case "11":
                        case "12": //陣雨
                            icon_img.setImageResource(R.drawable.showerumbrella);
                            break;
                        case "23": //狂風大作
                            icon_img.setImageResource(R.drawable.bluesetry23);
                            break;
                        case "24": //有風
                            icon_img.setImageResource(R.drawable.wind);
                            break;
                        case "25": //冷
                            icon_img.setImageResource(R.drawable.coldtemperature);
                            break;
                        case "26": //多雲
                            icon_img.setImageResource(R.drawable.clouds);
                            break;
                        case "29":
                        case "27": //晴間多雲（夜）
                            icon_img.setImageResource(R.drawable.mostlycloudnight);
                            break;
                        case "30":
                        case "28": //晴間多雲（日）
                            icon_img.setImageResource(R.drawable.mostlycloudy);
                            break;
                        case "33":
                        case "31": //清晰的（夜）
                            icon_img.setImageResource(R.drawable.clear_night);
                            break;
                        case "32":
                        case "34":
                        case "36":
                            icon_img.setImageResource(R.drawable.sun);
                            break;
                        case "37":
                        case "38":
                        case "39":
                        case "40":
                            icon_img.setImageResource(R.drawable.isolated);
                            break;

                        case "not available":
                            icon_img.setImageResource(R.drawable.transport1);
                            break;
                        default:
                            icon_img.setImageResource(R.drawable.transport1);
                    }
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);

        // 取得選單項目物件
        //search_item = menu.findItem(R.id.search_item);
        revert_item = menu.findItem(R.id.revert_item);
        delete_item = menu.findItem(R.id.delete_item);

        // 設定選單項目
        processMenu(null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (mActionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        // 使用參數取得使用者選擇的選單項目元件編號
        int itemId = item.getItemId();
        switch (itemId){

            // 取消所有已勾選的項目
            case R.id.revert_item:
                for (int i = 0; i < notesItemAdapter.getCount(); i++) {
                    NotesItem revert = notesItemAdapter.getItem(i);

                    if (revert.isSelected()) {
                        revert.setSelected(false);
                        notesItemAdapter.set(i, revert);
                    }
                }
                selectedCount = 0;
                processMenu(null);
                break;

            // 刪除
            case R.id.delete_item:
                // 沒有選擇
                if (selectedCount == 0) {
                    break;
                }
                // 建立與顯示詢問是否刪除的對話框
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                String message = getString(R.string.delete_item);
                dialog.setTitle(R.string.delete)
                        .setMessage(String.format(message, selectedCount));
                dialog.setPositiveButton(android.R.string.yes,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 刪除所有已勾選的項目
                                // 取得最後一個元素的編號
                                int index = notesItemAdapter.getCount() - 1;

                                while(index > -1) {
                                    NotesItem notesItem = notesItemAdapter.get(index);

                                    if (notesItem.isSelected()){
                                        notesItemAdapter.remove(notesItem);
                                        // 刪除資料庫中的記事資料
                                        notesItemDAO.delete(notesItem.getId());
                                    }
                                    index--;
                                }
                                // 通知資料改變
                                notesItemAdapter.notifyDataSetChanged();
                                selectedCount = 0;
                                processMenu(null);
                            }
                        });
                dialog.setNegativeButton(android.R.string.no, null);
                dialog.show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void netWork(View view){
        ConnectivityManager mConnectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();

        if(mNetworkInfo == null || !mNetworkInfo.isAvailable())
        {
            //網路尚未連線
            Toast.makeText(this, "網路尚未連線", Toast.LENGTH_SHORT).show();
            Snackbar snackbar = Snackbar.make(view, "網路尚未連線",
                    Snackbar.LENGTH_INDEFINITE)
                    .setDuration(8000);
            View snackBarView = snackbar.getView();
            snackBarView.setBackgroundColor(Color.parseColor("#FF4444"));
            snackbar.show();
        }
        else
        {
            //網路已連線
            Toast.makeText(this, "網路已連線", Toast.LENGTH_SHORT).show();
//            Snackbar snackbar = Snackbar.make(view, "網路已連線",
//                    Snackbar.LENGTH_INDEFINITE)
//                    .setDuration(5000);
//            View snackBarView = snackbar.getView();
//            snackBarView.setBackgroundColor(Color.parseColor("#2b8ade"));
//
//            snackbar.show();
        }

    }

    private void initView() {
        settings_Layout = (LinearLayout)findViewById(R.id.setting_layout);
        tag_Layout = (LinearLayout)findViewById(R.id.tag_layout);
        about_Layout = (LinearLayout)findViewById(R.id.about_layout);
        direction_Layout = (LinearLayout)findViewById(R.id.direction_layout);

        icon_img = (ImageView) findViewById(R.id.icon_img);
        weather = (TextView)findViewById(R.id.textWeather);
        weather_temp = (TextView)findViewById(R.id.textTemp);
        mListNote = (ListView)findViewById(R.id.listNote);
        mGarbageImg = (ImageView)findViewById(R.id.imageTruck);
        mLocationImg = (ImageView)findViewById(R.id.imageLocation);
        mInfoImg = (ImageView)findViewById(R.id.imageInfo);
        mNoteImg = (ImageView)findViewById(R.id.imageNote);
        // 註冊監聽物件
        mGarbageImg.setOnClickListener(mGarbageImgListener);
        mLocationImg.setOnClickListener(mLocationImgListener);
        mNoteImg.setOnClickListener(mNoteImgListener);
        mInfoImg.setOnClickListener(mInfoImgListener);

        // 註冊選單項目點擊監聽物件
        mListNote.setOnItemClickListener(itemListener);
        // 註冊選單項目長按監聽物件
        mListNote.setOnItemLongClickListener(itemLongListener);

        //側開式選單
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                R.string.app_name,R.string.app_name){
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mActionBarDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mActionBarDrawerToggle);

    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    private void initLayout() {
        settings_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GoogleAnalyticsButtonClick
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("SettingAction")
                        .setAction("Setting")
                        .build());
                startActivity(new Intent(MainActivity.this, ThemeToggle.class));
                mDrawerLayout.closeDrawers();

            }
        });
        tag_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GoogleAnalyticsButtonClick
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("ColorTagAction")
                        .setAction("ColorTag")
                        .build());
                // 啟動設定元件
                startActivity(new Intent(MainActivity.this, PrefActivity.class));
                mDrawerLayout.closeDrawers();
            }
        });
        about_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GoogleAnalyticsButtonClick
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("AboutAction")
                        .setAction("About")
                        .build());
                startActivity(new Intent(MainActivity.this, AboutActivity.class));
                mDrawerLayout.closeDrawers();
            }
        });
        direction_Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //GoogleAnalyticsButtonClick
                mTracker.send(new HitBuilders.EventBuilder()
                        .setCategory("DirectionAction")
                        .setAction("direction")
                        .build());
                startActivity(new Intent(MainActivity.this, DirectionsActivity.class));
                mDrawerLayout.closeDrawers();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle("關閉應用程式")
                .setMessage("確定要離開應用程式?")
                .setPositiveButton("確定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //後退按鈕中關閉整個應用程序 //啟動HomeScreen
                        Intent a = new Intent(Intent.ACTION_MAIN);
                        a.addCategory(Intent.CATEGORY_HOME);
                        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(a);
                    }

                })
                .setNegativeButton("取消", null)
                .show();

    }

    @Override
    protected void onPause() {
        super.onPause();
        //Log.e(TAG, "onPause: " );
    }

    @Override
    protected void onStop() {
        super.onStop();
        //finish();
       // Log.e(TAG, "onStop: " );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        finish();
       // Log.e(TAG, "onDestroy: " );
    }

}
