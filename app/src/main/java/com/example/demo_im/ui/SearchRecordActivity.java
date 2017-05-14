package com.example.demo_im.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.demo_im.R;
import com.example.demo_im.adapters.MessageSearchAdapter;
import com.example.demo_im.model.RelativeRecord;
import com.tencent.TIMConversationType;
import com.tencent.TIMMessage;

public class SearchRecordActivity extends Activity {
    private ListView listView;
    private MessageSearchAdapter adapter;
    private static RelativeRecord sRelativeRecord;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_record);
        context=this;
        listView=(ListView)findViewById(R.id.recordList);
        adapter=new MessageSearchAdapter(this,R.layout.item_message_search,sRelativeRecord,false);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TIMMessage timMessage=sRelativeRecord.getMessages().get(position);
                ChatActivity.navToChat(context,sRelativeRecord.getPeer(),TIMConversationType.C2C,timMessage);
            }
        });

    }

    public static void navToSearchRecord(Context context,RelativeRecord relativeRecord) {
        sRelativeRecord=relativeRecord;
        Intent intent = new Intent(context, SearchRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sRelativeRecord=null;
    }
}
