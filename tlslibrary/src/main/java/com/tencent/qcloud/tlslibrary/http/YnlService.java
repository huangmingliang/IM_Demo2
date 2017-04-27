package com.tencent.qcloud.tlslibrary.http;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by huangmingliang on 2017/4/27.
 */

public class YnlService {

    private static YnlService ynlService;
    private YnlAccountHelper loginHelper;
    private YnlAccountLoginService ynlAccountLoginService;
    private static int lastErrno = -1;

    public YnlService(){
        loginHelper=YnlAccountHelper.getInstance();
    }

    public static YnlService getInstance() {
        if (ynlService == null) {
            ynlService = new YnlService();
        }
        return ynlService;
    }

    public void initYnlService(Context context){
        loginHelper.init(context);
    }

    public void PwdLogin(String identifier, String password, YnlPwdLoginListener listener) {
        loginHelper.ynlLogin(identifier, password, listener);
    }

    public void initAccountLoginService(Context context,
                                        EditText txt_username,
                                        EditText txt_password,
                                        Button btn_login) {
        ynlAccountLoginService = new YnlAccountLoginService(context, txt_username, txt_password, btn_login);
    }

    public String getIdentifier(){
        return loginHelper.getIdentifier();
    }

    public String getUserSig(){
        return loginHelper.getUserSig();
    }

    public static void setLastErrno(int errno) {
        lastErrno = errno;
    }

    public static int getLastErrno() {
        return lastErrno;
    }

}
