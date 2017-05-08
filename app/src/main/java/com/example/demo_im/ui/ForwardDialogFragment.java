package com.example.demo_im.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.model.Conversation;
import com.example.demo_im.model.ImageMessage;
import com.example.demo_im.model.TextMessage;
import com.example.demo_im.utils.BitmapUtil;
import com.example.demo_im.utils.FileUtil;
import com.tencent.TIMImage;
import com.tencent.TIMImageElem;
import com.tencent.TIMImageType;
import com.tencent.TIMValueCallBack;
import com.tencent.qcloud.ui.CircleImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangmingliang on 2017/5/5.
 */

public class ForwardDialogFragment extends DialogFragment {
    private String TAG = ForwardDialogFragment.class.getSimpleName();
    private View view;
    private TextView send;
    private TextView cancel;
    private ImageView forwardImg;
    private TextView forwardText;
    private LinearLayout mContactsView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_forward, null);
        }
        send = (TextView) view.findViewById(R.id.send);
        cancel = (TextView) view.findViewById(R.id.cancel);
        forwardText = (TextView) view.findViewById(R.id.forwardText);
        forwardImg = (ImageView) view.findViewById(R.id.forwardImg);
        mContactsView = (LinearLayout) view.findViewById(R.id.contactsView);
        for (Conversation conversation:ForwardListActivity.sConversations){
            mContactsView.addView(getContactView(conversation,ForwardListActivity.sConversations.size()>1?false:true));
        }

        initForwardContent();
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent = new Intent();
               ArrayList<String> ids=new ArrayList<>();
                for (Conversation conversation:ForwardListActivity.sConversations){
                    ids.add(conversation.getIdentify());
                }
                intent.putStringArrayListExtra("ids",ids);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private LinearLayout getContactView(Conversation conversation,boolean isShowName) {
        LinearLayout mContactView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.include_item, null);
        CircleImageView circleImageView = (CircleImageView) mContactView.findViewById(R.id.friendImg);
        circleImageView.setPadding(0, 0, 0, 0);
        TextView textView = (TextView) mContactView.findViewById(R.id.friendName);
        circleImageView.setImageResource(conversation.getAvatar());
        textView.setText(conversation.getName());
        if (isShowName){
            textView.setVisibility(View.VISIBLE);
        }else {
            textView.setVisibility(View.GONE);
        }

        return mContactView;
    }

    private void initForwardContent() {
        if (ChatActivity.sMessage instanceof TextMessage) {
            forwardText.setVisibility(View.VISIBLE);
            forwardImg.setVisibility(View.GONE);
            forwardText.setText(ChatActivity.sMessage.getSummary());
        } else if (ChatActivity.sMessage instanceof ImageMessage) {
            forwardText.setVisibility(View.GONE);
            forwardImg.setVisibility(View.VISIBLE);
            TIMImageElem e = (TIMImageElem) ChatActivity.sMessage.getMessage().getElement(0);
            for (final TIMImage image : e.getImageList()) {
                if (image.getType() == TIMImageType.Thumb) {
                    final String uuid = image.getUuid();
                    if (FileUtil.isCacheFileExist(uuid)) {
                        forwardImg.setImageBitmap(BitmapUtil.createBitmap(uuid, 300));
                    } else {
                        image.getImage(new TIMValueCallBack<byte[]>() {
                            @Override
                            public void onError(int code, String desc) {//获取图片失败
                                //错误码code和错误描述desc，可用于定位请求失败原因
                                //错误码code含义请参见错误码表
                                Log.e(TAG, "getImage failed. code: " + code + " errmsg: " + desc);
                            }

                            @Override
                            public void onSuccess(byte[] data) {//成功，参数为图片数据
                                FileUtil.createFile(data, uuid);
                                forwardImg.setImageBitmap(BitmapUtil.createBitmap(uuid, 300));
                            }
                        });
                    }
                }
            }
        }
    }

}
