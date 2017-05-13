package com.admin.claire.garbag_truck;

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

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private ListView mListNote;
    private ImageView mGarbageImg;
    private ImageView mLocationImg;
    private ImageView mNoteImg;
    private ImageView mAlarmImg;

    private static String weatherUrl = "https://query.yahooapis.com/v1/public/yql?q=select%20item.condition%20from%20weather.forecast%20where%20woeid%20%3D%2055812231%20and%20u%3D%22c%22&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
    private String TAG = "weatherJSON";

    private TextView weather;
    private TextView weather_temp;
    private ImageView icon_img;
    private String code;
    private String temp;
    private String text;

    private ArrayAdapter<String> adapter;
    private static final String[]data = {
            "週日、三：停收垃圾及資源回收物(廚餘)",
            "週一、五：平面類：\n" +
                    "紙類\n" +
                    "舊衣類\n" +
                    "乾淨塑膠袋",
            "週二、四、六：立體類︰\n" +
                    "乾淨保麗龍\n" +
                    "一般類（瓶罐、容器、小家電等）"
    };

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
        mAlarmImg = (ImageView)findViewById(R.id.imageAlarm);
        mNoteImg = (ImageView)findViewById(R.id.imageNote);
        // 註冊監聽物件
        mGarbageImg.setOnClickListener(mGarbageImgListener);
        mLocationImg.setOnClickListener(mLocationImgListener);
        mNoteImg.setOnClickListener(mNoteImgListener);

    }

    private void initListView() {

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
                Toast.makeText(MainActivity.this, data[position],
                        Toast.LENGTH_SHORT).show();
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
                Toast.makeText(MainActivity.this, "Long:" + data[position],
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

    private View.OnClickListener mLocationImgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.click_animation));
            startActivity(new Intent(MainActivity.this, MapsActivity.class));
        }
    };

    private View.OnClickListener mNoteImgListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            v.startAnimation(AnimationUtils.loadAnimation(MainActivity.this,R.anim.click_animation));
            startActivity(new Intent(MainActivity.this, NotesActivity.class));
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
