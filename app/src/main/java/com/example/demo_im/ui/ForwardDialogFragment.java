package com.example.demo_im.ui;

import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.demo_im.R;
import com.example.demo_im.model.Conversation;
import com.tencent.qcloud.ui.CircleImageView;

/**
 * Created by huangmingliang on 2017/5/5.
 */

public class ForwardDialogFragment extends DialogFragment{
    private View view;
    private TextView send;
    private TextView cancel;
    private LinearLayout mContactsView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (view==null){
            view=LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_forward,null);
        }
        send=(TextView)view.findViewById(R.id.send);
        cancel=(TextView)view.findViewById(R.id.cancel);
        mContactsView=(LinearLayout)view.findViewById(R.id.contactsView);
        mContactsView.addView(getContactView(ForwardListActivity.sConversation));
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                getActivity().setResult(Activity.RESULT_OK);
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

    private LinearLayout getContactView(Conversation conversation){
        LinearLayout mContactView= (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.include_item,null);
        CircleImageView circleImageView=(CircleImageView)mContactView.findViewById(R.id.friendImg);
        TextView textView=(TextView)mContactView.findViewById(R.id.friendName);
        circleImageView.setImageResource(conversation.getAvatar());
        textView.setText(conversation.getName());
        return mContactView;
    }

}
