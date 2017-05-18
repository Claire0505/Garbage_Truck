package com.admin.claire.garbag_truck.broadcast;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.admin.claire.garbag_truck.NotesItem;
import com.admin.claire.garbag_truck.database.NotesItemDAO;

import java.util.Calendar;
import java.util.List;

public class InitAlarmReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        // 建立資料庫物件
        NotesItemDAO notesItemDAO = new NotesItemDAO(context.getApplicationContext());
        // 讀取資料庫所有記事資料
        List<NotesItem> notesItems = notesItemDAO.getAll();

        // 讀取目前時間
        long current = Calendar.getInstance().getTimeInMillis();

        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        for (NotesItem notesItem : notesItems) {
            long alarm = notesItem.getAlarmDatetime();

            // 如果沒有設定提醒或是提醒已經過期
            if (alarm == 0 || alarm <= current) {
                continue;
            }

            // 設定提醒
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            //alarmIntent.putExtra("title", notesItem.getTitle());
            // 加入記事編號資料
            intent.putExtra("id", notesItem.getId());

            PendingIntent pi = PendingIntent.getBroadcast(
                    context, (int)notesItem.getId(),
                    alarmIntent, PendingIntent.FLAG_ONE_SHOT);
            am.set(AlarmManager.RTC_WAKEUP, notesItem.getAlarmDatetime(), pi);
        }

    }
}
