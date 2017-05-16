package com.module.moments.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.module.moments.R;
import com.module.moments.bean.FavorItem;
import com.module.moments.utils.DatasUtil;
import com.module.moments.utils.DensityUtil;
import com.squareup.picasso.Picasso;
import com.tencent.qcloud.ui.CircleImageView;

import java.util.List;

/**
 * Created by huangmingliang on 2017/5/16.
 */

public class FavorListView extends LinearLayout {
    private List<FavorItem> datas;
    boolean isFavor = false;
    private OnFavorClickListener onFavorClickListener;

    public FavorListView(Context context) {
        super(context);
    }

    public FavorListView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDatas(List<FavorItem> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    public List<FavorItem> getDatas() {
        return datas;
    }

    public void notifyDataSetChanged(){
        setOrientation(HORIZONTAL);
        removeAllViews();
        LayoutInflater inflater=null;
        LayoutParams params=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity= Gravity.CENTER_VERTICAL;
        for (int i=0;i<datas.size();i++ ){
            if (i>2){
                break;
            }
            if (inflater==null){
                inflater=LayoutInflater.from(getContext());
            }
            LinearLayout view= (LinearLayout) inflater.inflate(R.layout.view_favor,null);
            CircleImageView circleImageView=(CircleImageView)view.findViewById(R.id.avatar);
            Picasso.with(getContext())
                    .load(datas.get(i).getUser().getHeadUrl())
                    .placeholder(R.drawable.icon_head)
                    .into(circleImageView);
            params.setMargins(DensityUtil.dip2px(getContext(),5.0f),0,0,0);
            view.setLayoutParams(params);
            final int temp = i;
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavorClickListener.onClick(temp);
                }
            });
            addView(view,i,params);
        }

        if (datas.size()>1){

            ImageView imageView=new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            for (FavorItem favorItem:datas){
                if (favorItem.getId()== DatasUtil.curUser.getId()){
                    isFavor=true;
                    break;
                }
            }
            if (isFavor){
                imageView.setImageResource(R.drawable.favor_green);
            }else {
                imageView.setImageResource(R.drawable.favor_gray);
            }
            imageView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavorClickListener.onFavor(!isFavor);
                }
            });
            addView(imageView,0,params);

        }
    }


    public interface OnFavorClickListener {
         void onClick(int position);
         void onFavor(boolean isFavor);
    }

    public void setOnFavorClickListener(OnFavorClickListener onFavorClickListener) {
        this.onFavorClickListener = onFavorClickListener;
    }


}
