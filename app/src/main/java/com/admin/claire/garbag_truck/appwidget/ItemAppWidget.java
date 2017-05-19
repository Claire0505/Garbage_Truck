package com.admin.claire.garbag_truck.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.admin.claire.garbag_truck.MainActivity;
import com.admin.claire.garbag_truck.NotesItem;
import com.admin.claire.garbag_truck.R;
import com.admin.claire.garbag_truck.database.NotesItemDAO;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link ItemAppWidgetConfigureActivity ItemAppWidgetConfigureActivity}
 */
public class ItemAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // 讀取小工具儲存的記事編號
        long id = ItemAppWidgetConfigureActivity.loadItemPref(context, appWidgetId);

        // 建立小工具畫面元件
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.item_app_widget);

        // 讀取指定編號的記事物件
        NotesItemDAO notesItemDAO = new NotesItemDAO(context.getApplicationContext());
        NotesItem notesItem = notesItemDAO.get(id);

        // 設定小工具畫面顯示記事標題
        views.setTextViewText(R.id.appwidget_text,
                notesItem != null ? notesItem.getTitle(): "NA");

        // 點選小工具畫面的記事標題後，啟動記事應用程式
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pending = PendingIntent.getActivity(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.appwidget_text, pending);
        // 更新小工具
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        // 刪除小工具已經儲存的記事編號
        for (int i = 0 ;i < N; i++) {
            ItemAppWidgetConfigureActivity.deleteItemPref(context, appWidgetIds[i]);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

