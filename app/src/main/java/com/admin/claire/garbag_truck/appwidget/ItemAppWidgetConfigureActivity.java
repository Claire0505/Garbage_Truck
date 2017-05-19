package com.admin.claire.garbag_truck.appwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.admin.claire.garbag_truck.NotesItem;
import com.admin.claire.garbag_truck.NotesItemAdapter;
import com.admin.claire.garbag_truck.R;
import com.admin.claire.garbag_truck.database.NotesItemDAO;

import java.util.List;

/**
 * The configuration screen for the {@link ItemAppWidget ItemAppWidget} AppWidget.
 */
public class ItemAppWidgetConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    private static final String PREFS_NAME =
            "com.admin.claire.garbag_truck.appwidget.ItemAppWidget";

    private static final String PREF_PREFIX_KEY = "appwidget_";
    // 選擇小工具使用的記事項目
    private ListView mListNote;
    private NotesItemAdapter notesItemAdapter;
    private List<NotesItem> notesItems;
    private NotesItemDAO notesItemDAO;


    public ItemAppWidgetConfigureActivity() {
        super();
    }

    // 選擇記事項目
    AdapterView.OnItemClickListener itemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    final Context context = ItemAppWidgetConfigureActivity.this;
                    // 讀取與儲存選擇的記事物件
                    NotesItem notesItem = notesItemAdapter.getItem(position);
                    saveItemPref(context, mAppWidgetId, notesItem.getId());

                    AppWidgetManager appWidgetManager =
                            AppWidgetManager.getInstance(context);

                    ItemAppWidget.updateAppWidget(
                            context, appWidgetManager, mAppWidgetId);

                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                    setResult(RESULT_OK, resultValue);

                    finish();

                }
            };

    // 儲存選擇的記事編號
    static void saveItemPref(Context context, int appWidgetId, long id) {

        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putLong(PREF_PREFIX_KEY + appWidgetId, id);
        prefs.commit();
    }

    // 讀取記事編號
    static long loadItemPref(Context context, int appWidgetId) {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, 0);
        long idValue = prefs.getLong(PREF_PREFIX_KEY + appWidgetId, 0);

        return idValue;

    }

    // 刪除記事編號
    static void deleteItemPref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        setResult(RESULT_CANCELED);

        // 改為使用應用程式主畫面
        setContentView(R.layout.activity_main);
        // 建立與設定選擇小工具使用的記事項目需要的物件
        mListNote = (ListView)findViewById(R.id.listNote);
        notesItemDAO = new NotesItemDAO(getApplicationContext());
        notesItems = notesItemDAO.getAll();
        notesItemAdapter = new NotesItemAdapter(this, R.layout.singleitem, notesItems);
        mListNote.setAdapter(notesItemAdapter);
        mListNote.setOnItemClickListener(itemClickListener);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
                                        AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }

}

