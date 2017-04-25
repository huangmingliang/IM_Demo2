package com.example.demo_im.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.model.FriendProfile;
import com.tencent.qcloud.ui.CircleImageView;

import java.util.List;

/**
 * Created by huangmingliang on 2017/4/24.
 */

public class ContactAdapter extends BaseAdapter {

    private String TAG="ContactAdapter";
    private Context context;
    private List<FriendProfile> friends;
    final int HEADER_SIZE =2;

    public ContactAdapter(Context context, List<FriendProfile> friends){
        this.context=context;
        this.friends =friends;
    }
    @Override
    public int getCount() {
        return friends.size()+ HEADER_SIZE;
    }

    @Override
    public Object getItem(int position) {
        if (position< HEADER_SIZE){
            return "HEADER";
        }else {
            return friends.get(position-HEADER_SIZE);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.e(TAG,"HML position="+position);
        ViewHolder viewHolder;
        if (convertView==null){
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_friend, null);
            viewHolder.sectionLayout=(LinearLayout)convertView.findViewById(R.id.sectionLayout);
            viewHolder.friendSection=(TextView)convertView.findViewById(R.id.friendSection);
            viewHolder.friendImg=(CircleImageView)convertView.findViewById(R.id.friendImg);
            viewHolder.friendName=(TextView)convertView.findViewById(R.id.friendName);
            convertView.setTag(viewHolder);
        }else {
            viewHolder= (ViewHolder) convertView.getTag();
        }
        if (getItem(position).equals("HEADER")){
            viewHolder.sectionLayout.setVisibility(View.GONE);
            viewHolder.friendName.setText(position==0?"新的朋友":"群聊");
            viewHolder.friendImg.setImageResource(R.drawable.head_other);
        }else {
            FriendProfile friend = friends.get(position-HEADER_SIZE);
            //根据position获取分类的首字母的char ascii值
            int relativePosition=position-HEADER_SIZE;
            Log.e(TAG,"HML relativePosition="+relativePosition);
            int section = getSectionForPosition(relativePosition);
            //如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
            if(relativePosition == getPositionForSection(section)){
                viewHolder.sectionLayout.setVisibility(View.VISIBLE);
                viewHolder.friendSection.setText(friend.getSection()+"");
            }else{
                viewHolder.sectionLayout.setVisibility(View.GONE);
            }
            viewHolder.friendName.setText(friend.getRemark());

            viewHolder.friendImg.setImageResource(R.drawable.head_other);
        }
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
        return friends.get(position).getSection();
    }

    /**
     * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
     */
    public int getPositionForSection(int section) {
        for (int i = 0; i < getCount()-HEADER_SIZE; i++) {
            char firstChar = friends.get(i).getSection();
            if (firstChar == section) {
                return i;
            }
        }
        return -1;
    }
}
