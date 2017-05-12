package com.example.demo_im.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.model.SearchMessage;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by huangmingliang on 2017/5/12.
 */

public class MessageSearchAdapter extends ArrayAdapter<SearchMessage>{
    private int resource;
    private ViewHolder viewHolder;

    public MessageSearchAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<SearchMessage> objects) {
        super(context, resource, objects);
        this.resource=resource;
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
            viewHolder.message=(TextView)convertView.findViewById(R.id.message);
            viewHolder.messageSrc=(TextView)convertView.findViewById(R.id.messageSrc);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        SearchMessage searchMessage=getItem(position);
        if (searchMessage.isShowTitle()){
            viewHolder.title.setVisibility(View.VISIBLE);
            viewHolder.textContent.setText(searchMessage.getTitleContent());
        }else {
            viewHolder.title.setVisibility(View.GONE);
        }
        viewHolder.message.setText(searchMessage.getMessage());
        viewHolder.messageSrc.setText(searchMessage.getMessageSrc());
        Picasso.with(getContext())
                .load(searchMessage.getAvatarUrl())
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
        TextView message;
        TextView messageSrc;
    }
}
