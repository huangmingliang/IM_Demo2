package com.example.demo_im.adapters;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.model.Conversation;
import com.example.demo_im.model.Message;
import com.squareup.picasso.Picasso;
import com.tencent.qcloud.ui.CircleImageView;

import java.util.List;

/**
 * Created by huangmingliang on 2017/5/5.
 */

public class ForwardAdapter extends ArrayAdapter<Conversation> {
    private List<Conversation> conversations;
    private int resource;
    private Context context;
    public ForwardAdapter(@NonNull Context context, @LayoutRes int resource, List<Conversation> conversations) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
        this.conversations=conversations;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder holder=null;
        Conversation conversation=conversations.get(position);
        if (convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(resource,null);
            holder.imageView=(CircleImageView)convertView.findViewById(R.id.friendImg);
            holder.textView=(TextView)convertView.findViewById(R.id.friendName);
            convertView.setTag(holder);
        }else {
            convertView.getTag();
        }
        holder.textView.setText(conversation.getName());
        Picasso.with(context)
                .load(conversation.getAvatar())
                .placeholder(R.drawable.head_man)
                .centerCrop()
                .into(holder.imageView);
        return convertView;
    }

    class ViewHolder{
        CircleImageView imageView;
        TextView textView;
    }
}
