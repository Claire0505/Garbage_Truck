package com.admin.claire.garbag_truck;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by claire on 2017/5/11.
 */

public class GarbageJSONParser {


    private static String TAG;


    static List<GarbageTruck> garbageTruckList;

    public static List<GarbageTruck> parseData(String s) {

        JSONArray jsonArray = null;
        GarbageTruck garbageTruck = null;

        try {

            jsonArray = new JSONArray(s);
            garbageTruckList = new ArrayList<>();

            JSONObject jsonObject = new JSONObject(s);
            jsonArray = jsonObject.getJSONObject("result").getJSONArray("results");

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);

                garbageTruck = new GarbageTruck();

                garbageTruck.set_id(object.getString("_id"));
                garbageTruck.setTitle(object.getString("title"));
                garbageTruck.setContent(object.getString("content"));
                garbageTruck.setLat(object.getString("lat"));
                garbageTruck.setLng(object.getString("lng"));
                garbageTruck.setModifydate(object.getString("modifydate"));

                garbageTruckList.add(garbageTruck);
                Log.e(TAG, "parseData: "+  garbageTruckList.add(garbageTruck) );
            }

            return garbageTruckList;

        } catch (final JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
