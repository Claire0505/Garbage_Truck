package com.admin.claire.garbag_truck;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.SearchRecentSuggestions;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.analytics.HitBuilders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.admin.claire.garbag_truck.MainActivity.mTracker;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREFS_NAME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_DARK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PINK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PURPLE_THEME;


public class ListViewActivity extends AppCompatActivity {

    private ListView lv;
    //臺北市垃圾清運點位資訊
    private final String TPETrashURL = "http://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=aa9c657c-ea6f-4062-a645-b6c973d45564";
    private final String TPETrashURL1 = "https://www.dropbox.com/s/f3yb3rvny6pwrj8/opendata_trash.json?dl=1";
    String TPE_GarbageTruckURL = "https://www.dropbox.com/s/frwxakmnd9xziax/opendata_trash20181012.txt?dl=1";
    ArrayList<HashMap<String, String>> garbagetrucklist;

    //private ListAdapter adapter;
    private SimpleAdapter adapter;
    private String TAG = "JSON_TRASH";

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
        setContentView(R.layout.activity_list_view);

        // 增加廣告
        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);

        lv = (ListView)findViewById(R.id.list);
        lv.setOnItemClickListener(onClickListView);
        getData();

        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, "請輸入行政區查詢", Snackbar.LENGTH_LONG)
                .show();

    }

    private void getData(){
        //覆寫JsonObjectRequest將編碼轉成UTF8，必免亂碼
        Utf8JsonRequest jsonObjectRequest = new Utf8JsonRequest(
                Request.Method.GET,TPE_GarbageTruckURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // Log.d(TAG, "onResponse: " + response.toString() );
                        parserJson(response);
                        adapter = new SimpleAdapter(
                                ListViewActivity.this,
                                garbagetrucklist,
                                R.layout.list_item_card,
                                new String[]{"Title","Content","Lng","Lat"},
                                new int[]{R.id.title, R.id.content, R.id.lng, R.id.lat});
                        lv.setAdapter(adapter);
                        lv.setTextFilterEnabled(true);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "onErrorResponse: " + error.toString() );
                        Toast.makeText(ListViewActivity.this,error.toString(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
//                TPETrashURL1,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                       // Log.e(TAG, "onResponse: " + response.toString() );
//                        parserJson(response);
//
//                        adapter = new SimpleAdapter(
//                                ListViewActivity.this,
//                                garbagetrucklist,
//                                R.layout.list_item_card,
//                                new String[]{"title","content","lng","lat"},
//                                new int[]{R.id.title, R.id.content, R.id.lng, R.id.lat});
//                        lv.setAdapter(adapter);
//                        lv.setTextFilterEnabled(true);
//
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        Log.e(TAG, "onErrorResponse: " + error.toString() );
//                        Toast.makeText(ListViewActivity.this,error.toString(),
//                                Toast.LENGTH_SHORT).show();
//                    }
//                }
//        );


        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    private void parserJson(JSONObject jsonObject) {

        try {
            garbagetrucklist = new ArrayList<>();

            JSONArray data = jsonObject.getJSONObject("result").getJSONArray("results");
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);

                String unit = object.getString("Unit");
                String title = object.getString("Title");
                String content = object.getString("Content");
                String lat = object.getString("Lat");
                String lng = object.getString("Lng");
                String modifydate = object.getString("ModifyDate");
                String diffgrid = object.getString("_diffgr:id");
                String msdatarowOrder = object.getString("_msdata:rowOrder");

                HashMap<String, String> garbagetruck = new HashMap<>();
                garbagetruck.put("Unit", unit);
                garbagetruck.put("Title", title);
                garbagetruck.put("Content", content);
                garbagetruck.put("Lat", lat);
                garbagetruck.put("Lng", lng);
                garbagetruck.put("ModifyDate", modifydate);
                garbagetruck.put("_diffgr:id", diffgrid);
                garbagetruck.put("_msdata:rowOrder", msdatarowOrder);

                garbagetrucklist.add(garbagetruck);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        final MenuItem searchItem = menu.findItem(R.id.search);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        //final SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager != null ? searchManager.getSearchableInfo(getComponentName()) : null);

        // 這邊讓icon可以還原到搜尋的icon
        searchView.setIconifiedByDefault(true);
        //searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false); //是否要點選搜尋圖示後再打開輸入框
        searchView.setFocusable(false);
        searchView.requestFocusFromTouch();      //要點選後才會開啟鍵盤輸入
        searchView.setSubmitButtonEnabled(false);//輸入框後是否要加上送出的按鈕
        searchView.setQueryHint("輸入行政區查詢:內湖區...."); //輸入框沒有值時要顯示的提示文字

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                   //GoogleAnalyticsButtonClick
                   mTracker.send(new HitBuilders.EventBuilder()
                           .setCategory("GarbageListQueryTextChange")
                           .setAction("QueryTextChang: " + newText)
                           .build());

                   //過濾列表資料

                  // adapter.getFilter().filter("垃圾清運點：臺北市" + newText);
                if (TextUtils.isEmpty(newText)){
                    adapter.getFilter().filter("");
                   // Log.i(TAG, "onQueryTextChange: Empty String");
                    lv.clearTextFilter();
                }else {
                    adapter.getFilter().filter("垃圾清運點：臺北市" + newText);

                }
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //GoogleAnalyticsButtonClick
            mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("GarbageListAction")
                    .setAction("GarbageListDetail")
                    .build());

            TextView text1 = (TextView)view.findViewById(R.id.title);
            TextView text2 = (TextView)view.findViewById(R.id.content);
            TextView text3 = (TextView)view.findViewById(R.id.lng);
            TextView text4 = (TextView)view.findViewById(R.id.lat);

            String title = text1.getText().toString();
            String content = text2.getText().toString();
            String lng = text3.getText().toString();
            String lat = text4.getText().toString();

            Intent intent = new Intent(ListViewActivity.this, Garbagetruck_List_Activity.class);
            //傳送資料
            intent.putExtra("Title", title);
            intent.putExtra("Content", content);
            intent.putExtra("Lng", lng);
            intent.putExtra("Lat", lat);
            startActivity(intent);

//           Toast.makeText(MainActivity.this, "點選第 " + (position + 1)
//                   + "個\n內容:" + title , Toast.LENGTH_SHORT).show();

        }
    };


}
