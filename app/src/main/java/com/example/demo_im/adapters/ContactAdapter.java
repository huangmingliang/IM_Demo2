package com.example.demo_im.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.model.FriendProfile;
import com.squareup.picasso.Picasso;
import com.tencent.qcloud.ui.CircleImageView;

import java.util.List;

/**
 * Created by huangmingliang on 2017/4/24.
 */

public class ContactAdapter extends ArrayAdapter<FriendProfile> {

    private String TAG="ContactAdapter";
    public static final int HEADER_SIZE =1;
    private int resource;
    ViewHolder viewHolder = null;

    public ContactAdapter(Context context,int resource, List<FriendProfile> friends){
        super(context,resource,friends);
        this.resource=resource;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(resource, null);
            viewHolder.sectionLayout=(LinearLayout)convertView.findViewById(R.id.sectionLayout);
            viewHolder.friendSection=(TextView)convertView.findViewById(R.id.friendSection);
            viewHolder.friendImg=(CircleImageView)convertView.findViewById(R.id.friendImg);
            viewHolder.friendName=(TextView)convertView.findViewById(R.id.friendName);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

            FriendProfile friend = getItem(position);
            //根据position获取分类的首字母的char ascii值
            int relativePosition=position;
            int section = getSectionForPosition(relativePosition);
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if(relativePosition == getPositionForSection(section)){
                viewHolder.sectionLayout.setVisibility(View.VISIBLE);
                viewHolder.friendSection.setText(friend.getSection()+"");
            }else{
                viewHolder.sectionLayout.setVisibility(View.GONE);
            }
            viewHolder.friendName.setText(friend.getRemark());

            //viewHolder.friendImg.setImageResource(R.drawable.head_other);
            Picasso.with(getContext())
                    .load(friend.getAvatarUrl())
                    .placeholder(friend.getAvatarRes())
                    .error(friend.getAvatarRes())
                    .centerCrop()
                    .into(viewHolder.friendImg);
        return convertView;
    }

    class ViewHolder{
        LinearLayout sectionLayout;
        TextView friendSection;
        CircleImageView friendImg;
        TextView friendName;
    }

    /**
     * 根据ListView的当前位置获取分类的首字母的char ascii值
     */
    public int getSectionForPosition(int position) {
        return getItem(position).getSection();
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount(); i++) {
            char firstChar = getItem(i).getSection();
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }
}
