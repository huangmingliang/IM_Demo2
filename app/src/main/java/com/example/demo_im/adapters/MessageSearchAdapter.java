package com.example.demo_im.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.model.RelativeRecord;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by huangmingliang on 2017/5/12.
 */

public class MessageSearchAdapter extends ArrayAdapter<RelativeRecord>{
    private String TAG=MessageSearchAdapter.class.getSimpleName();
    private int resource;
    List<RelativeRecord> relativeRecords;
    private ViewHolder viewHolder;

    public MessageSearchAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<RelativeRecord> relativeRecords) {
        super(context, resource, relativeRecords);
        this.resource=resource;
        this.relativeRecords=relativeRecords;
    }

    @Override
    public int getCount() {
        return relativeRecords.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(getContext()).inflate(resource,null);
            viewHolder.title=(LinearLayout)convertView.findViewById(R.id.itemTitle);
            viewHolder.textContent=(TextView)convertView.findViewById(R.id.titleContent);
            viewHolder.avatar=(ImageView)convertView.findViewById(R.id.avatar);
            viewHolder.messageSrc =(TextView)convertView.findViewById(R.id.messageSrc);
            viewHolder.messageDes =(TextView)convertView.findViewById(R.id.messageDes);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        RelativeRecord relativeRecord=getItem(position);
        if (position==0){
            viewHolder.title.setVisibility(View.VISIBLE);
            viewHolder.textContent.setText(getContext().getString(R.string.message_records));
        }else {
            viewHolder.title.setVisibility(View.GONE);
        }
        viewHolder.messageSrc.setText(relativeRecord.getName());
        String strFormat=getContext().getString(R.string.message_describe);
        viewHolder.messageDes.setText(String.format(strFormat,relativeRecord.getMessageCount()));
        Log.e(TAG,"hml url="+relativeRecord.getAvatarUrl());
        Picasso.with(getContext())
                .load(relativeRecord.getAvatarUrl())
                .placeholder(R.drawable.head_man)
                .resize(70,70)
                .error(R.drawable.head_man)
                .centerCrop()
                .into(viewHolder.avatar);

        return convertView;
    }

    class ViewHolder{
        LinearLayout title;
        TextView textContent;
        ImageView avatar;
        TextView messageSrc;
        TextView messageDes;
    }
}
