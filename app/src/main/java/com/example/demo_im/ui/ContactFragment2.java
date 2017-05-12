package com.example.demo_im.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.demo_im.R;
import com.example.demo_im.adapters.ContactAdapter;
import com.example.demo_im.model.FriendProfile;
import com.example.demo_im.model.FriendshipInfo;
import com.tencent.qcloud.ui.SideIndexBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by huangmingliang on 2017/4/24.
 */

public class ContactFragment2 extends Fragment implements View.OnClickListener,Observer{

    private String TAG="ContactFragment2";
    private View view;
    private View headerView;
    private ListView mFriendListView;
    private TextView mDialogTextView;
    private SideIndexBar mSideIndexBar;
    private List<FriendProfile> friends=new ArrayList<>();
    private ContactAdapter adapter;
    private Listener listener=new Listener();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.e(TAG,"hml onCreateView");
        if (view==null){
            view = inflater.inflate(R.layout.fragment_contact2, container, false);
            initHeaderView(inflater);
            mFriendListView=(ListView)view.findViewById(R.id.friendList);
            mDialogTextView=(TextView)view.findViewById(R.id.text_dialog);
            mSideIndexBar=(SideIndexBar)view.findViewById(R.id.index_bar);
            mSideIndexBar.setTextDialog(mDialogTextView);
            adapter=new ContactAdapter(getActivity(),R.layout.item_friend,friends);
            FriendshipInfo.getInstance().getFriendProfiles(listener);
            mFriendListView.addHeaderView(headerView);
            mFriendListView.setAdapter(adapter);
            mFriendListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (position==0){

                    }else {
                        friends.get(position-1).onClick(getActivity());
                    }
                }
            });

            mSideIndexBar.setOnLetterChangedListener(new SideIndexBar.OnLetterChangedListener() {
                @Override
                public void onChanged(String s, int position) {
                    int section=s.toCharArray()[0];
                    int firstPosition=adapter.getPositionForSection(section);
                    if (firstPosition!=-1){
                        int target;
                        int first=mFriendListView.getFirstVisiblePosition();
                        int last=mFriendListView.getLastVisiblePosition();
                        if (firstPosition>first){
                            target=last+firstPosition-first-1;
                        }else if (firstPosition<first){
                            target=firstPosition;
                        }else {
                            target=first;
                        }
                        mFriendListView.smoothScrollToPosition(target);
                    }
                }
            });
            FriendshipInfo.getInstance().addObserver(this);
            adapter.notifyDataSetChanged();
        }
        return view;
    }

    @Override
    public void onResume(){
        super.onResume();
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
        FriendshipInfo.getInstance().getFriendProfiles(listener);
    }

    class Listener implements FriendshipInfo.OnRefreshFriendProfilesListener{

        @Override
        public void onRefreshFriendProfiles(List<FriendProfile> profiles) {
            friends.clear();
            friends.addAll(profiles);
            adapter.notifyDataSetChanged();
        }
    }

    private void initHeaderView(LayoutInflater inflater){
        headerView=inflater.inflate(R.layout.item_contact_header,null);
        headerView.findViewById(R.id.contactSearch).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toSearch();
            }
        });

        headerView.findViewById(R.id.newFriend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FriendshipManageMessageActivity.navToFriendshipManageMessage(getActivity());
            }
        });

        headerView.findViewById(R.id.group_chat);
        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "敬请期待...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toSearch(){
        getActivity().findViewById(R.id.home).setVisibility(View.GONE);
        getActivity().findViewById(R.id.homeSearch).setVisibility(View.VISIBLE);
    }

}
