package com.example.demo_im.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.demo_im.R;
import com.example.demo_im.adapters.ContactAdapter;
import com.example.demo_im.model.FriendProfile;
import com.example.demo_im.model.FriendshipInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by huangmingliang on 2017/4/24.
 */

public class ContactFragment2 extends Fragment implements View.OnClickListener,Observer{

    private View view;
    private ListView mFriendListView;
    private List<FriendProfile> friends=new ArrayList<>();
    private ContactAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view==null){
            view = inflater.inflate(R.layout.fragment_contact2, container, false);
            mFriendListView=(ListView)view.findViewById(R.id.friendList);
            refresh();
            adapter=new ContactAdapter(getActivity(),friends);
            mFriendListView.setAdapter(adapter);
            FriendshipInfo.getInstance().addObserver(this);
            adapter.notifyDataSetChanged();
        }
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
        refresh();
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void update(Observable o, Object arg) {
        if (o instanceof FriendshipInfo){
            refresh();
            adapter.notifyDataSetChanged();
        }
    }

    private void refresh(){
        friends.clear();
        friends.addAll(FriendshipInfo.getInstance().getFriendProfiles());
    }
}
