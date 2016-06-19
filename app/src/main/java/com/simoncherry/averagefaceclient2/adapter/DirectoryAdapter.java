package com.simoncherry.averagefaceclient2.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.simoncherry.averagefaceclient2.R;
import com.simoncherry.averagefaceclient2.bean.DirectoryBean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Simon on 2016/6/19.
 */
public class DirectoryAdapter extends BaseAdapter {

    private Context ctx;
    private LayoutInflater inflater;
    private List<DirectoryBean> list;

    public DirectoryAdapter(Context context, List<DirectoryBean> list){
        this.ctx = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_listview_faceset, null);
            holder = new ViewHolder();
            holder.img_icon = (ImageView) convertView.findViewById(R.id.img_icon);
            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
            holder.tv_date = (TextView) convertView.findViewById(R.id.tv_date);
            holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        DirectoryBean bean = list.get(position);
        int imgID = bean.getImgID();
        String name = bean.getFileName();
        int count = bean.getFileCount();
        Long date = bean.getFileDate();
        String time = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date(date));

        holder.img_icon.setImageResource(imgID);
        holder.tv_name.setText(name);
        holder.tv_count.setText("文件数量：" + String.valueOf(count));
        holder.tv_date.setText(time);

        return convertView;
    }

    private class ViewHolder{
        ImageView img_icon;
        TextView tv_name;
        TextView tv_date;
        TextView tv_count;
    }
}
