package com.example.demo_im.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.demo_im.R;
import com.example.demo_im.model.Message;
import com.example.demo_im.model.SearchMessage;
import com.tencent.TIMElem;
import com.tencent.TIMElemType;
import com.tencent.TIMMessage;
import com.tencent.TIMTextElem;
import com.tencent.imcore.TextElem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/5/13.
 */

public class DbUtil {
    private static DbUtil dbUtil;
    private final String DB_NAME = "im";
    private final int DB_VERSION = 1;
    private final String TABLE_MESSAGE = "message_records";
    private final String TABLE_FRIEND = "friends";
    private final String KEY_ID = "_id";
    private final String KEY_IDENTIFIER = "identifier";
    private final String KEY_TXT_MESSAGE = "txt_message";
    private final String KEY_NICE = "nick";
    private final String KEY_REMARK = "remark";
    private final String KEY_FACE_URL = "face_url";
    private DataBasesHelper helper;
    private SQLiteDatabase db;
    private static Context mContext;

    public DbUtil(Context context) {
        helper = new DataBasesHelper(context, DB_NAME, null, DB_VERSION);
        db = helper.getReadableDatabase();
    }

    public static DbUtil getInstance(Context context) {
        synchronized (DbUtil.class) {
            if (dbUtil == null) {
                mContext = context.getApplicationContext();
                dbUtil = new DbUtil(mContext);
            }
        }
        return dbUtil;
    }

    public void insertMessage(TIMMessage message) {
        for (int i = 0; i < message.getElementCount(); ++i) {
            TIMElem timElem = message.getElement(i);
            if (timElem.getType() == TIMElemType.Text) {
                ContentValues values = new ContentValues();
                values.put(KEY_IDENTIFIER, message.getConversation().getIdentifer());
                values.put(KEY_TXT_MESSAGE, ((TIMTextElem) timElem).getText().toString());
                db.insert(TABLE_MESSAGE, null, values);
            }
        }

    }

    public List<SearchMessage> getSearchMessage(String str) {
        List<SearchMessage> searchMessageList = new ArrayList<>();
        Cursor cursor = db.query(TABLE_MESSAGE, new String[]{KEY_IDENTIFIER, KEY_TXT_MESSAGE}, KEY_TXT_MESSAGE + " like ?", new String[]{"%" + str + "%"}, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                SearchMessage searchMessage = new SearchMessage(null);
                searchMessage.setIdentify(cursor.getString(cursor.getColumnIndex(KEY_IDENTIFIER)));
                searchMessage.setMessage(cursor.getString(cursor.getColumnIndex(KEY_TXT_MESSAGE)));
                searchMessage.setMessageSrc(mContext.getString(R.string.message_records));
                if (cursor.isFirst()) {
                    searchMessage.setShowTitle(true);
                    searchMessage.setTitleContent(mContext.getString(R.string.message_records));
                }
                searchMessageList.add(searchMessage);

            } while (cursor.moveToNext());
        }
        return searchMessageList;
    }
}
