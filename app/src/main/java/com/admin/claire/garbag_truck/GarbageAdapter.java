package com.admin.claire.garbag_truck;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;


/**
 * Created by claire on 2017/5/11.
 */

public class GarbageAdapter extends BaseAdapter {

    private Context context;
    private List<GarbageTruck> garbageTruckList;
    private LayoutInflater inflater = null;

    public GarbageAdapter(Context context , List<GarbageTruck> list) {
        this.context = context;
        this.garbageTruckList = list;
        inflater = LayoutInflater.from(context);
    }

    public class ViewHolder {
        TextView _id;
        TextView _title;
        TextView _content;
        TextView _lng;
        TextView _lat;
        TextView _modifydate;
    }

    @Override
    public int getCount() {
        return garbageTruckList.size();
    }

    @Override
    public Object getItem(int position) {
        return garbageTruckList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final GarbageTruck garbageTruck = garbageTruckList.get(position);
        final ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_templates , null);
            holder = new ViewHolder();



        }
        return convertView;
    }
}
