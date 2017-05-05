package com.example.demo_im.ui;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.demo_im.R;
import com.example.demo_im.model.Message;

/**
 * Created by huangmingliang on 2017/5/5.
 */

public class ForwardDialogFragment extends DialogFragment{
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=LayoutInflater.from(this.getActivity()).inflate(R.layout.fragment_forward,null);
        Bundle bundle=getArguments();

        return view;
    }

    interface ForwardMessageListerner{
        public abstract Message getForwardMessage();
    }
}
