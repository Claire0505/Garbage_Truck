package com.admin.claire.garbag_truck;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by claire on 2017/5/16.
 */

public class NotesItemAdapter extends ArrayAdapter<NotesItem> {

    // 畫面資源編號
    private int resource;
    // 包裝的記事資料
    private List<NotesItem> items;

    public NotesItemAdapter(Context context, int resource, List<NotesItem> items) {
        super(context, resource, items);
        this.resource = resource;
        this.items = items;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LinearLayout itemView;
        // 讀取目前位置的記事物件
        final NotesItem item = getItem(position);

        if (convertView == null) {
            //建立項目畫面元件
            itemView = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater li = (LayoutInflater)getContext().getSystemService(inflater);
            li.inflate(resource, itemView, true);
        }
        else {
            itemView = (LinearLayout)convertView;
        }

        // 讀取記事顏色、已選擇、標題與日期時間元件
        RelativeLayout typeColor = (RelativeLayout)itemView.findViewById(R.id.type_color);
        ImageView selectedItem = (ImageView)itemView.findViewById(R.id.selected_item);
        TextView titleView = (TextView)itemView.findViewById(R.id.notesTitle_text);
        TextView dateView = (TextView)itemView.findViewById(R.id.date_text);
        TextView notifyView = (TextView)itemView.findViewById(R.id.notify_text);

        // 設定記事顏色
        GradientDrawable background = (GradientDrawable)typeColor.getBackground();
        background.setColor(item.getColor().parseColor());

        // 設定標題與日期時間
        titleView.setText(item.getTitle());
        dateView.setText(item.getLocaleDateTime());

        if (item.getAlarmDatetime() != 0){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm EEE");
            String str = df.format(item.getAlarmDatetime());
            notifyView.setText("提醒:" + str);

        } else {
            notifyView.setText("");
        }

        // 設定是否已選擇
        selectedItem.setVisibility(item.isSelected() ? View.VISIBLE : View.INVISIBLE);

        return itemView;
    }

    // 設定指定編號的記事資料
    public void set(int index, NotesItem item){
        if (index >= 0 && index < items.size()) {
            items.set(index, item);
            notifyDataSetChanged();
        }
    }

    // 讀取指定編號的記事資料
    public NotesItem get(int index){
        return items.get(index);
    }
}
