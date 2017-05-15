package com.admin.claire.garbag_truck;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

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
    private ArrayList<String> data = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getData();
        initView();
        initListView();
    }

    private void initView() {
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

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 如果被啟動的Activity元件,傳回確定的結果
        if (resultCode == Activity.RESULT_OK) {
            //讀取標題
            String titleText = data.getStringExtra("titleText");

            // 如果是新增記事
            if (requestCode == 0){
                //加入標題項目
                this.data.add(titleText);
                // 通知資料已經改變，ListView元件才會重新顯示
                adapter.notifyDataSetChanged();
            }
            // 如果是修改記事
            else if (requestCode == 1){
                // 讀取記事編號
                int position = data.getIntExtra("position", -1);

                if (position != -1) {
                    // 設定標題項目
                    this.data.set(position, titleText);
                    // 通知資料已經改變，ListView元件才會重新顯示
                    adapter.notifyDataSetChanged();
                }

            }

        }

    }

    private void initListView() {
        // 加入範例資料
        data.add("週日、三：\n 停收垃圾及資源回收物(廚餘)");
        data.add("週一、五：\n平面類：" +
                "紙類\n" +
                "舊衣類\n" +
                "乾淨塑膠袋");
        data.add("週二、四、六：\n立體類︰ " +
                "乾淨保麗龍\n" +
                "一般類（瓶罐、容器、小家電等）");
         adapter = new ArrayAdapter<String>(this, R.layout.list_item_templates,
                 R.id.text_Note, data);

        mListNote.setAdapter(adapter);


        // 建立選單項目點擊監聽物件
        AdapterView.OnItemClickListener itemListener = new AdapterView.OnItemClickListener() {
            // 第一個參數是使用者操作的ListView物件
            // 第二個參數是使用者選擇的項目
            // 第三個參數是使用者選擇的項目編號，第一個是0
            // 第四個參數在這裡沒有用途
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 使用Action名稱建立啟動另一個Activity元件需要的Intent物件
                Intent intent = new Intent("com.admin.claire.garbag_truck.EDIT_ITEM");

                // 設定記事編號與標題
                intent.putExtra("position", position);
                intent.putExtra("titleText", data.get(position));

                // 呼叫「startActivityForResult」，第二個參數「1」表示執行修改
                startActivityForResult(intent, 1);
            }
        };
        // 註冊選單項目點擊監聽物件
        mListNote.setOnItemClickListener(itemListener);

        // 建立選單項目長按監聽物件
        AdapterView.OnItemLongClickListener itemLongListener =
                new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                Toast.makeText(MainActivity.this, "Long:" + data.get(position),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        };
        // 註冊選單項目長按監聽物件
        mListNote.setOnItemLongClickListener(itemLongListener);

    }

    private View.OnClickListener mGarbageImgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //onClick時的動畫旋轉效果
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.click_animation));
            startActivity(new Intent(MainActivity.this, ListViewActivity.class));
        }
    };

   //定位所有明細
    private View.OnClickListener mLocationImgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.click_animation));
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        }
    };

    //新增記事
    private View.OnClickListener mNoteImgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

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

}
