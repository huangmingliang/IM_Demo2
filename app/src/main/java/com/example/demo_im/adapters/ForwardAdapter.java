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
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.model.Conversation;
import com.example.demo_im.model.Message;
import com.example.demo_im.ui.ForwardListActivity;
import com.squareup.picasso.Picasso;
import com.tencent.qcloud.ui.CircleImageView;

import java.util.List;

/**
 * Created by huangmingliang on 2017/5/5.
 */

public class ForwardAdapter extends ArrayAdapter<Conversation> {
    private String TAG=ForwardAdapter.class.getSimpleName();
    private List<Conversation> conversations;
    private int resource;
    private Context context;
    public ForwardAdapter(Context context, @LayoutRes int resource, List<Conversation> conversations) {
        super(context, resource);
        this.context=context;
        this.resource=resource;
        this.conversations=conversations;

    }

    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        Conversation conversation=conversations.get(position);
        Log.e(TAG,"hml name="+conversation.getName());
        if (convertView==null){
            holder=new ViewHolder();
            convertView= LayoutInflater.from(context).inflate(resource,null);
            holder.imageView=(CircleImageView)convertView.findViewById(R.id.friendImg);
            holder.textView=(TextView)convertView.findViewById(R.id.friendName);
            holder.checkBox=(CheckBox)convertView.findViewById(R.id.checkbox);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        holder.textView.setText(conversation.getName());
        Picasso.with(context)
                .load(conversation.getAvatar())
                .placeholder(R.drawable.head_man)
                .resize(70,70)
                .error(R.drawable.head_man)
                .centerCrop()
                .into(holder.imageView);
        if (ForwardListActivity.isCheckBoxVisible){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(conversation.getSelected());
        }
        return convertView;
    }

    class ViewHolder{
        public CircleImageView imageView;
        public TextView textView;
        public CheckBox checkBox;
    }

}
