package com.admin.claire.garbag_truck.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.admin.claire.garbag_truck.Colors;
import com.admin.claire.garbag_truck.NotesActivity;
import com.admin.claire.garbag_truck.NotesItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by claire on 2017/5/17.
 */
// 資料功能類別
public class NotesItemDAO {
    // 表格名稱
    public static final String TABLE_NAME = "notesitem";
    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String DATETIME_COLUMN = "datetime";
    public static final String COLOR_COLUMN = "color";
    public static final String TITLE_COLUMN = "title";
    public static final String CONTENT_COLUMN = "content";
    public static final String FILENAME_COLUMN = "filename";
    public static final String LASTMODIFY_COLUMN = "lastmodify";
    // 提醒日期時間
    public static final String ALARMDATETIME_COLUMN = "alarmdatetime";

    // 使用上面宣告的變數建立表格的SQL指令
    public static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    DATETIME_COLUMN + " INTEGER NOT NULL, " +
                    COLOR_COLUMN + " INTEGER NOT NULL, " +
                    TITLE_COLUMN + " TEXT NOT NULL, " +
                    CONTENT_COLUMN + " TEXT NOT NULL, " +
                    FILENAME_COLUMN + " TEXT, " +
                    LASTMODIFY_COLUMN + " INTEGER, " +
                    ALARMDATETIME_COLUMN + " INTEGER)";

    // 資料庫物件
    private SQLiteDatabase db;

    // 建構子，一般的應用都不需要修改
    public NotesItemDAO(Context context) {
        db = MyDBHelper.getDatabase(context);
    }

    // 關閉資料庫
    public void close() {
        db.close();
    }

    // 新增參數指定的物件
    public NotesItem insert (NotesItem notesItem) {
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(DATETIME_COLUMN, notesItem.getDatetime());
        cv.put(COLOR_COLUMN, notesItem.getColor().parseColor());
        cv.put(TITLE_COLUMN, notesItem.getTitle());
        cv.put(CONTENT_COLUMN, notesItem.getContent());
        cv.put(FILENAME_COLUMN, notesItem.getFileName());
        cv.put(LASTMODIFY_COLUMN, notesItem.getLastModify());
        // 提醒日期時間
        cv.put(ALARMDATETIME_COLUMN, notesItem.getAlarmDatetime());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        notesItem.setId(id);
        // 回傳結果
        return notesItem;
    }

    // 修改參數指定的物件
    public boolean update(NotesItem notesItem){
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(DATETIME_COLUMN, notesItem.getDatetime());
        cv.put(COLOR_COLUMN, notesItem.getColor().parseColor());
        cv.put(TITLE_COLUMN, notesItem.getTitle());
        cv.put(CONTENT_COLUMN, notesItem.getContent());
        cv.put(FILENAME_COLUMN, notesItem.getFileName());
        cv.put(LASTMODIFY_COLUMN, notesItem.getLastModify());
        // 提醒日期時間
        cv.put(ALARMDATETIME_COLUMN, notesItem.getAlarmDatetime());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + notesItem.getId();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where,null) > 0;

    }

    // 刪除參數指定編號的資料
    public boolean delete(long id) {
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where , null) > 0;
    }

    // 讀取所有記事資料
    public List<NotesItem> getAll() {
        List<NotesItem> result = new ArrayList<>();
        Cursor cursor = db.query(  // KEY_ID + " DESC"  改orderBy條件可以做排序 大-->小
                TABLE_NAME, null, null, null, null, null, KEY_ID + " DESC" , null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 取得指定編號的資料物件
    public NotesItem get(long id){
        // 準備回傳結果用的物件
        NotesItem notesItem = null;
        // 使用編號為查詢條件
        String where = KEY_ID + "=" + id;
        // 執行查詢
        Cursor result = db.query(TABLE_NAME, null, where, null, null, null, null, null);

        // 如果有查詢結果
        if (result.moveToFirst()) {
            // 讀取包裝一筆資料的物件
            notesItem = getRecord(result);
        }

        // 關閉Cursor物件
        result.close();
        // 回傳結果
        return notesItem;

    }

    // 把Cursor目前的資料包裝為物件
    public NotesItem getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        NotesItem result = new NotesItem();

        result.setId(cursor.getLong(0));
        result.setDatetime(cursor.getLong(1));
        result.setColor(NotesActivity.getColors(cursor.getInt(2)));
        result.setTitle(cursor.getString(3));
        result.setContent(cursor.getString(4));
        result.setFileName(cursor.getString(5));
        result.setLastModify(cursor.getLong(6));
        // 提醒日期時間
        result.setAlarmDatetime(cursor.getLong(7));

        // 回傳結果
        return result;
    }

    // 取得資料數量
    public int getCount(){
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }
        return result;
    }

    // 建立範例資料
    public void sample() {

        NotesItem notesItem = new NotesItem(0, new Date().getTime(), Colors.RED,
                "週日、三：\n停收垃圾及資源回收物(廚餘)",
                "停收垃圾及資源回收物(廚餘)"," ", 0 );


        NotesItem notesItem1 = new NotesItem(0, new Date().getTime(),Colors.GREEN,
                "週一、五：\n平面類：紙類,舊衣類,乾淨塑膠袋",
                "平面類：\n紙類,舊衣類,乾淨塑膠袋"," ", 0 );

        NotesItem notesItem2 = new NotesItem(0, new Date().getTime(),Colors.ORANGE,
                "週二、四、六：\n立體類︰乾淨保麗龍, 一般類（瓶罐、容器、小家電等）",
                "立體類︰乾淨保麗龍\n一般類（瓶罐、容器、小家電等）"," ", 0 );


        insert(notesItem);
        insert(notesItem1);
        insert(notesItem2);

    }

}
