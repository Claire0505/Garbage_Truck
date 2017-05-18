package com.admin.claire.garbag_truck.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;


import com.admin.claire.garbag_truck.FileUtil;
import com.admin.claire.garbag_truck.NotesItem;

import com.admin.claire.garbag_truck.R;
import com.admin.claire.garbag_truck.database.NotesItemDAO;

import java.io.File;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 讀取記事標題
//        String title = intent.getStringExtra("title");
//        //顯示訊息框
//        Toast.makeText(context, title , Toast.LENGTH_SHORT).show();


        // 讀取記事編號
        long id = intent.getLongExtra("id", 0);

        if (id != 0) {
            sendNotify(context, id);
        }
    }

    private void sendNotify(Context context, long id) {
        // 建立資料庫物件
        NotesItemDAO itemDAO = new NotesItemDAO(context.getApplicationContext());
        // 讀取指定編號的記事物件
       NotesItem item = itemDAO.get(id);

        // 建立照片檔案物件
        File file = new File(FileUtil.getExternalStorageDir(FileUtil.APP_DIR),
                "P" + item.getFileName() + ".jpg");

        // 是否儲存照片檔案
        boolean isPicture = (item.getFileName() != null &&
                item.getFileName().length() > 0 &&
                file.exists());

        // 取得NotificationManager物件
        NotificationManager nm = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        // 如果有儲存照片檔案
        if (isPicture) {
            // 建立Notification.Builder物件，因為要設定大型圖片樣式
            // 所以不能使用NotificationCompat.Builder
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(android.R.drawable.btn_star_big_on)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(context.getString(R.string.app_name))
                    .setDefaults(Notification.DEFAULT_ALL);

            // 建立大型圖片樣式物件
            Notification.BigPictureStyle bigPictureStyle =
                    new Notification.BigPictureStyle();
            // 設定圖片與簡介
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            bigPictureStyle.bigPicture(bitmap)
                    .setSummaryText(item.getTitle());
            // 設定樣式為大型圖片
            builder.setStyle(bigPictureStyle);
            // 發出通知
            nm.notify((int)item.getId(), builder.build());
        }
        // 如果沒有儲存照片檔案
        else {
            // 建立NotificationCompat.Builder物件
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(context);
            // 設定圖示、時間、內容標題和內容訊息
            builder.setSmallIcon(android.R.drawable.btn_star_big_on)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(item.getTitle())
                    .setDefaults(NotificationCompat.DEFAULT_ALL);
            // 發出通知
            nm.notify((int)item.getId(), builder.build());
        }
    }
}

