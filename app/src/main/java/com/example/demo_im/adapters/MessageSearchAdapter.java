package com.example.demo_im.adapters;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.model.NomalConversation;
import com.example.demo_im.model.RelativeRecord;
import com.example.demo_im.utils.Foreground;
import com.squareup.picasso.Picasso;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMFaceElem;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangmingliang on 2017/5/12.
 */

public class MessageSearchAdapter extends ArrayAdapter{
    private String TAG=MessageSearchAdapter.class.getSimpleName();
    private int resource;
    private List<RelativeRecord> relativeRecords;
    private ViewHolder viewHolder;
    private boolean isShowTitle=true;
    private RelativeRecord relativeRecord;

    public MessageSearchAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<RelativeRecord> relativeRecords) {
        super(context, resource, relativeRecords);
        this.resource=resource;
        this.relativeRecords=relativeRecords;
    }

    public MessageSearchAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull RelativeRecord relativeRecord,boolean isShowTitle) {
        super(context, resource, relativeRecord.getMessages());
        this.relativeRecord=relativeRecord;
        this.resource=resource;
        this.relativeRecord=relativeRecord;
        this.isShowTitle=isShowTitle;
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
        if (isShowTitle){
            RelativeRecord relativeRecord= (RelativeRecord) getItem(position);
            if (relativeRecord.getMessageCount()>1){
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
            }else {
                if (position==0){
                    viewHolder.title.setVisibility(View.VISIBLE);
                    viewHolder.textContent.setText(getContext().getString(R.string.message_records));
                }else {
                    viewHolder.title.setVisibility(View.GONE);
                }
                viewHolder.messageSrc.setText(relativeRecord.getName());
                List<TIMElem> elems = new ArrayList<>();
                TIMMessage message=relativeRecord.getMessages().get(0);
                boolean hasText = false;
                for (int i = 0; i < message.getElementCount(); ++i){
                    elems.add(message.getElement(i));
                    if (message.getElement(i).getType() == TIMElemType.Text){
                        hasText = true;
                    }
                }
                SpannableStringBuilder stringBuilder = getString(elems, getContext(),relativeRecord.getKeyStr());
                if (!hasText){
                    stringBuilder.insert(0,"");
                }
                viewHolder.messageDes.setText(stringBuilder);
                int avatarSize= (int) getContext().getResources().getDimension(R.dimen.item_img_width);
                Picasso.with(getContext())
                        .load(relativeRecord.getAvatarUrl())
                        .placeholder(R.drawable.head_man)
                        .resize(avatarSize,avatarSize)
                        .error(R.drawable.head_man)
                        .centerCrop()
                        .into(viewHolder.avatar);
            }

        }else {
            TIMMessage message=(TIMMessage) getItem(position);
            boolean hasText = false;
            viewHolder.title.setVisibility(View.GONE);
            viewHolder.messageSrc.setText(relativeRecord.getName());
            List<TIMElem> elems = new ArrayList<>();
            for (int i = 0; i < message.getElementCount(); ++i){
                elems.add(message.getElement(i));
                if (message.getElement(i).getType() == TIMElemType.Text){
                    hasText = true;
                }
            }
            SpannableStringBuilder stringBuilder = getString(elems, getContext(),relativeRecord.getKeyStr());
            if (!hasText){
                stringBuilder.insert(0,"");
            }
            viewHolder.messageDes.setText(stringBuilder);
            Picasso.with(getContext())
                    .load(relativeRecord.getAvatarUrl())
                    .placeholder(R.drawable.head_man)
                    .resize(70,70)
                    .error(R.drawable.head_man)
                    .centerCrop()
                    .into(viewHolder.avatar);
            
        }
        return convertView;
    }

    public static SpannableStringBuilder getString(List<TIMElem> elems, Context context,String str) {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (int i = 0; i < elems.size(); ++i) {
            switch (elems.get(i).getType()) {
                case Face:
                    TIMFaceElem faceElem = (TIMFaceElem) elems.get(i);
                    int startIndex = stringBuilder.length();
                    try {
                        AssetManager am = context.getAssets();
                        InputStream is = am.open(String.format("emoticon/%d.png", faceElem.getIndex()));
                        if (is == null) continue;
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        Matrix matrix = new Matrix();
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        float scale = context.getResources().getDimension(R.dimen.message_emotion_size) / width;
                        matrix.postScale(scale, scale);
                        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                width, height, matrix, true);
                        ImageSpan span = new ImageSpan(context, resizedBitmap, ImageSpan.ALIGN_BOTTOM);
                        stringBuilder.append(String.valueOf(faceElem.getIndex()));
                        stringBuilder.setSpan(span, startIndex, startIndex + getNumLength(faceElem.getIndex()), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        is.close();
                    } catch (IOException e) {

                    }
                    break;
                case Text:
                    TIMTextElem textElem = (TIMTextElem) elems.get(i);
                    String text=textElem.getText();
                    int start=text.indexOf(str);
                    int end=start+str.length();
                    SpannableStringBuilder styleStr=new SpannableStringBuilder(text);
                    styleStr.setSpan(new ForegroundColorSpan(ContextCompat.getColor(context,R.color.green)),start,end, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
                    stringBuilder.append(styleStr);
                    break;
            }

        }
        return stringBuilder;
    }

    private static int getNumLength(int n){
        return String.valueOf(n).length();
    }

    class ViewHolder{
        LinearLayout title;
        TextView textContent;
        ImageView avatar;
        TextView messageSrc;
        TextView messageDes;
    }
}
