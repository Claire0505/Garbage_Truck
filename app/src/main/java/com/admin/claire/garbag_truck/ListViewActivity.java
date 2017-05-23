package com.admin.claire.garbag_truck;

import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ListViewActivity extends AppCompatActivity {

    private ListView lv;
    //臺北市垃圾清運點位資訊
    private final String TPETrashURL = "http://data.taipei/opendata/datalist/apiAccess?scope=resourceAquire&rid=aa9c657c-ea6f-4062-a645-b6c973d45564";
    private final String TPETrashURL1 = "https://www.dropbox.com/s/f3yb3rvny6pwrj8/opendata_trash.json?dl=1";
    ArrayList<HashMap<String, String>> garbagetrucklist;

    //private ListAdapter adapter;
    private SimpleAdapter adapter;
    private String TAG = "JSON_TRASH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        //啟用<- up按鈕
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        lv = (ListView)findViewById(R.id.list);
        lv.setOnItemClickListener(onClickListView);
        getData();

    }

    private void getData(){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                TPETrashURL1,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // Log.e(TAG, "onResponse: " + response.toString() );
                        parserJson(response);

                        adapter = new SimpleAdapter(
                                ListViewActivity.this,
                                garbagetrucklist,
                                R.layout.list_item_card,
                                new String[]{"title","content","lng","lat"},
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
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonObjectRequest);

    }

    private void parserJson(JSONObject jsonObject) {

        try {
            garbagetrucklist = new ArrayList<>();

            JSONArray data = jsonObject.getJSONObject("result").getJSONArray("results");
            for (int i = 0; i < data.length(); i++) {
                JSONObject object = data.getJSONObject(i);

                String id = object.getString("_id");
                String title = object.getString("title");
                String content = object.getString("content");
                String lat = object.getString("lat");
                String lng = object.getString("lng");
                String modifydate = object.getString("modifydate");

                HashMap<String, String> garbagetruck = new HashMap<>();
                garbagetruck.put("_id", id);
                garbagetruck.put("title", title);
                garbagetruck.put("content", content);
                garbagetruck.put("lat", lat);
                garbagetruck.put("lng", lng);
                garbagetruck.put("modifydate", modifydate);

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
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        // final SearchView searchView = (SearchView)menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        // 這邊讓icon可以還原到搜尋的icon
        searchView.setIconifiedByDefault(true);
        //searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false); //是否要點選搜尋圖示後再打開輸入框
        searchView.setFocusable(false);
        searchView.requestFocusFromTouch();      //要點選後才會開啟鍵盤輸入
        searchView.setSubmitButtonEnabled(false);//輸入框後是否要加上送出的按鈕
        searchView.setQueryHint("輸入行政區查詢: 內湖區..."); //輸入框沒有值時要顯示的提示文字


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //過濾列表資料
                adapter.getFilter().filter("垃圾清運點：臺北市" + newText);

                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private AdapterView.OnItemClickListener onClickListView = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
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
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("lng", lng);
            intent.putExtra("lat", lat);
            startActivity(intent);

//           Toast.makeText(MainActivity.this, "點選第 " + (position + 1)
//                   + "個\n內容:" + title , Toast.LENGTH_SHORT).show();

        }
    };

}
