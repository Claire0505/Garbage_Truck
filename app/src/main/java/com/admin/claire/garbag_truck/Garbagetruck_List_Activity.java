package com.admin.claire.garbag_truck;


import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREFS_NAME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_DARK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PINK_THEME;
import static com.admin.claire.garbag_truck.preference.ThemeToggle.PREF_PURPLE_THEME;


public class Garbagetruck_List_Activity extends FragmentActivity
        implements OnMapReadyCallback ,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private TextView mTilte, mContent, mLng, mLat, mDistance;
    private GoogleMap mMap;
    private String TAG = "MAP: ";

    // Google API用戶端物件
    private GoogleApiClient googleApiClient;
    // Location請求物件
    private LocationRequest locationRequest;
    private LocationManager locationManager;

    // 記錄目前最新的位置
    private Location currentLocation;
    // 顯示目前與儲存位置的標記物件
    private Marker currentMarker;
    // 定位設備授權請求代碼
    private static final int REQUEST_FINE_LOCATION_PERMISSION = 100;


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
        setContentView(R.layout.activity_garbagetruck__list_);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapfrag);
        mapFragment.getMapAsync(this);

        // 建立Google API用戶端物件
        configGoogleApiClient();
        // 建立Location請求物件
        // configLocationRequest();

        locationManager = (LocationManager)getSystemService(LOCATION_SERVICE);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mTilte = (TextView)findViewById(R.id.title_Text);
        mContent = (TextView)findViewById(R.id.content_Text);
        mLng = (TextView)findViewById(R.id.lng_Text);
        mLat = (TextView)findViewById(R.id.lat_Text);
        //取得傳遞過來的資料
        Intent intent = this.getIntent();
        String title = intent.getStringExtra("Title");
        String content = intent.getStringExtra("Content");
        String lng = intent.getStringExtra("Lng");
        String lat = intent.getStringExtra("Lat");

        mTilte.setText(title);
        mContent.setText(content);
        String strLng = lng.substring(0, lng.indexOf(".") + 6);
        String strLat = lat.substring(0, lng.indexOf(".") + 6);

        mLng.setText(getResources().getString(R.string.lng)+ strLng);
        mLat.setText(getResources().getString(R.string.lat)+ strLat);

        Double lngD  = Double.valueOf(lng);
        Double latD = Double.valueOf(lat);

        LatLng garbage = new LatLng(latD , lngD);

        // Log.e(TAG, "onMapReady: " + latD + " , " + lngD );

        mMap.addMarker(new MarkerOptions()
                .position(garbage)
                .title(title)
                .snippet(content)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.garbagetruck1)));


        //建立位置的座標物件
        LatLng place = garbage;
        //移動地圖
        moveMap(place);

    }

    // 移動地圖到參數指定的位置
    private void moveMap(LatLng place) {
        // 建立地圖攝影機的位置物件
        CameraPosition cameraPosition =
                new CameraPosition.Builder()
                        .target(place)
                        .zoom(16)
                        .build();

        // 使用動畫的效果移動地圖
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    // ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        // 已經連線到Google Services
        // 啟動位置更新服務
        Toast.makeText(Garbagetruck_List_Activity.this, "Google API 連線成功", Toast.LENGTH_SHORT).show();
        //啟用定位權限
        Location location = enableLocationAndGetLastLocation(true);
        if (location != null) {
            Toast.makeText(Garbagetruck_List_Activity.this, "成功取得上一次定位", Toast.LENGTH_SHORT).show();
            onLocationChanged(location);
        } else {
            Toast.makeText(Garbagetruck_List_Activity.this, "沒有上一次定位的資料", Toast.LENGTH_SHORT).show();
        }
    }

    // ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int cause) {
        // Google Services無故連線中斷
        switch (cause) {
            case CAUSE_NETWORK_LOST:
                Toast.makeText(Garbagetruck_List_Activity.this, "網路斷線，無法定位", Toast.LENGTH_SHORT).show();
                break;
            case CAUSE_SERVICE_DISCONNECTED:
                Toast.makeText(Garbagetruck_List_Activity.this, "Google API 異常，無法定位",
                        Toast.LENGTH_SHORT).show();
                break;
        }
    }

    // OnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Google Services連線失敗
        // ConnectionResult參數是連線失敗的資訊
        int errorCode = connectionResult.getErrorCode();

        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(this, R.string.google_play_service_missing,
                    Toast.LENGTH_LONG).show();
        }
    }

    // LocationListener
    @Override
    public void onLocationChanged(Location location) {
        // 位置改變
        // Location參數是目前的位置
        currentLocation = location;
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        // 設定目前位置的標記
        if (currentMarker == null) {
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));

//            //印出我的座標-經度緯度
//            Log.e("TAG", "我的座標 - 經度 : "
//                    + location.getLongitude() + "  , 緯度 : " + location.getLatitude() );

            mLng = (TextView)findViewById(R.id.lng_Text);
            mLat = (TextView)findViewById(R.id.lat_Text);
            //取得傳遞過來的資料
            Intent intent = this.getIntent();
            String lng = intent.getStringExtra("Lng");
            String lat = intent.getStringExtra("Lat");
            Double lngD  = Double.valueOf(lng);
            Double latD = Double.valueOf(lat);

           //畫出兩點間的線條
            PolylineOptions polylineOpt = new PolylineOptions()
                    .width(10)
                    .color(Color.BLUE);
            ArrayList<LatLng> listLatLng = new ArrayList<LatLng>();
            listLatLng.add(new LatLng(location.getLatitude(), location.getLongitude()));
            listLatLng.add(new LatLng(latD, lngD));
            polylineOpt.addAll(listLatLng);
            Polyline polylineRoute = mMap.addPolyline(polylineOpt);
            polylineRoute.setVisible(true);

            //帶入使用者及垃圾車經緯度可計算出距離
            Double distance = Distance(location.getLongitude(),location.getLatitude(), lngD,latD);

            mDistance = (TextView)findViewById(R.id.distance_Text);
            mDistance.setText(getResources().getString(R.string.distance) + " "
                    + distance + getResources().getString(R.string.meter));

            //Log.e(TAG, "距離為: "+ distance );
            //Toast.makeText(this, "距離為: "+ distance , Toast.LENGTH_LONG).show();


            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    marker.showInfoWindow();
                }
            });
        }
        else {
            currentMarker.setPosition(latLng);
        }
        // 移動地圖到目前的位置
       // moveMap(latLng);
    }


    //帶入使用者及垃圾車經緯度可計算出距離
    public double Distance(double longitude1, double latitude1, double longitude2,double latitude2)
    {
        double radLatitude1 = latitude1 * Math.PI / 180;
        double radLatitude2 = latitude2 * Math.PI / 180;
        double l = radLatitude1 - radLatitude2;
        double p = longitude1 * Math.PI / 180 - longitude2 * Math.PI / 180;
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(l / 2), 2)
                + Math.cos(radLatitude1) * Math.cos(radLatitude2)
                * Math.pow(Math.sin(p / 2), 2)));
        distance = distance * 6378137.0;
        distance = Math.round(distance * 10000) / 10000;

        return distance ;
    }


    // 建立Google API用戶端物件
    private synchronized void configGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // 建立Location請求物件
    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置資訊的間隔時間為一秒（1000ms）
        locationRequest.setInterval(1000);
        // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
        locationRequest.setFastestInterval(1000);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        googleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 連線到Google API用戶端
        if (!googleApiClient.isConnected() && currentMarker != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 移除位置請求服務
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 移除Google API用戶端連線
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    //啟用定位權限和最後一次定位記錄
    private Location enableLocationAndGetLastLocation(boolean on) {
        if (ContextCompat.checkSelfPermission(Garbagetruck_List_Activity.this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            //這項功能尚未取得使用者的同意
            //開始執行徵詢使用者的流程
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    Garbagetruck_List_Activity.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder altDlgBuilder =
                        new AlertDialog.Builder(Garbagetruck_List_Activity.this);
                altDlgBuilder.setTitle("提示");
                altDlgBuilder.setMessage("App 需要啟動定位功能。");
                altDlgBuilder.setIcon(android.R.drawable.ic_dialog_info);
                altDlgBuilder.setCancelable(false);
                altDlgBuilder.setPositiveButton("確定",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //顯示詢問使用者是否同意功能權限的對話盒
                                //使用者答覆後會執行 onRequestPermissionResult()
                                ActivityCompat.requestPermissions(Garbagetruck_List_Activity.this,
                                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_FINE_LOCATION_PERMISSION);
                            }
                        });
                altDlgBuilder.show();
                return null;
            } else {
                //顯示詢問使用者是否同意功能權限的對話盒
                //使用者答覆後會執行callback方法 onRequestPermissionResult()
                ActivityCompat.requestPermissions(Garbagetruck_List_Activity.this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        REQUEST_FINE_LOCATION_PERMISSION);
                return null;
            }
        }
        //這項功能之前已經取得使用者的同意，可以直接使用
        Location lastLocation = null;
        if (on) {
            //取得上一次定位資料
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            //準備一個LocationRequest物件，設定定位參數，在啟動定位時使用
            locationRequest = LocationRequest.create();
            //設定兩次定位之間的時間間隔，單位是千分之一秒
            locationRequest.setInterval(5000);
            //二次定位之間的最大距離，單位是公尺
            locationRequest.setSmallestDisplacement(5);

            //判斷使用者是否啟用了GPS定位
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                Toast.makeText(this, "尚未開啟定位服務，請檢查定位服務是否有開啟",
                        Toast.LENGTH_SHORT).show();
            }

            //啟動定位，如果GPS功能有開啟，優先使用GPS定位，否則使用網路定位
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                Toast.makeText(Garbagetruck_List_Activity.this, "使用GPS定位", Toast.LENGTH_SHORT).show();

            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                Toast.makeText(Garbagetruck_List_Activity.this, "使用網路定位", Toast.LENGTH_SHORT).show();
            }

            //啟動定位功能
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
        } else {
            //停止定位功能
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
            Toast.makeText(Garbagetruck_List_Activity.this, "停止定位", Toast.LENGTH_SHORT).show();
        }

        return lastLocation;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //檢查收到的權限要求編號是否和我們送出的相同
        if (requestCode == REQUEST_FINE_LOCATION_PERMISSION) {
            if (grantResults.length != 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //再檢查一次就會進入同意的狀態，並且順利啟動
                Location location = enableLocationAndGetLastLocation(true);

                if (location != null) {
                    Toast.makeText(Garbagetruck_List_Activity.this, "成功取得上一次定位",
                            Toast.LENGTH_SHORT).show();
                    //更新地圖的定位
                    onLocationChanged(location);
                } else {
                    Toast.makeText(Garbagetruck_List_Activity.this, "沒有上一次定位的資料",
                            Toast.LENGTH_SHORT).show();

                    return;
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}



