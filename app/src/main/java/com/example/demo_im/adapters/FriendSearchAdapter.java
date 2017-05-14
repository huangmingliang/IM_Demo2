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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.model.FriendProfile;
import com.squareup.picasso.Picasso;
import com.tencent.qcloud.ui.CircleImageView;

import java.util.List;

/**
 * Created by Administrator on 2017/5/13.
 */

public class FriendSearchAdapter extends ArrayAdapter<FriendProfile>{

    private String TAG=FriendSearchAdapter.class.getSimpleName();
    private int resource;
    private ViewHolder viewHolder = null;

    public FriendSearchAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<FriendProfile> friendProfiles) {
        super(context, resource, friendProfiles);
        this.resource=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(resource, null);
            viewHolder.itemTitle=(LinearLayout)convertView.findViewById(R.id.itemTitle);
            viewHolder.titleContent=(TextView)convertView.findViewById(R.id.titleContent);
            viewHolder.friendImg=(CircleImageView)convertView.findViewById(R.id.friendImg);
            viewHolder.friendName=(TextView)convertView.findViewById(R.id.friendName);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        FriendProfile friend = getItem(position);
        if(position==0){
            viewHolder.itemTitle.setVisibility(View.VISIBLE);
            viewHolder.titleContent.setText(getContext().getString(R.string.message_contact));
        }else{
            viewHolder.itemTitle.setVisibility(View.GONE);
        }
        viewHolder.friendName.setText(friend.getRemark());
        Picasso.with(getContext())
                .load(friend.getAvatarUrl())
                .placeholder(friend.getAvatarRes())
                .error(friend.getAvatarRes())
                .centerCrop()
                .into(viewHolder.friendImg);
        return convertView;
    }

    class ViewHolder{
        LinearLayout itemTitle;
        TextView titleContent;
        CircleImageView friendImg;
        TextView friendName;
    }
}
