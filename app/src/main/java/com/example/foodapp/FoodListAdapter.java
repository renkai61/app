package com.example.foodapp;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.foodapp.FoodItem;
import java.util.ArrayList;

public class FoodListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<FoodItem> dataList;
    private LayoutInflater inflater;

    public FoodListAdapter(Context context, ArrayList<FoodItem> dataList) {
        this.context = context;
        this.dataList = dataList;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dataList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_food, parent, false);
            holder = new ViewHolder();
            holder.imageViewPhoto = convertView.findViewById(R.id.imageViewPhoto);
            holder.textViewInfo = convertView.findViewById(R.id.textViewInfo);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        FoodItem item = dataList.get(position);

        holder.textViewInfo.setText(item.name + " (" + item.type + ") - 數量: " + item.quantity + " - 到期: " + item.expiryDate);

        if (item.photoUri != null && !item.photoUri.isEmpty()) {
            try {
                holder.imageViewPhoto.setImageURI(Uri.parse(item.photoUri));
            } catch (Exception e) {
                holder.imageViewPhoto.setImageResource(R.drawable.ic_launcher_foreground); // 預設圖片，放你自己的資源
            }
        } else {
            holder.imageViewPhoto.setImageResource(R.drawable.ic_launcher_foreground); // 預設圖片
        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView imageViewPhoto;
        TextView textViewInfo;
    }

}
