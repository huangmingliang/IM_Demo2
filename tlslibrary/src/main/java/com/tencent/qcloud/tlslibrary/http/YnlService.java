package com.tencent.qcloud.tlslibrary.http;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.service.AccountLoginService;

/**
 * Created by huangmingliang on 2017/4/27.
 */

public class YnlService {

    private static YnlService ynlService;
    private YnlAccountHelper loginHelper;
    private YnlAccountLoginService ynlAccountLoginService;

    public static YnlService getInstance() {
        if (ynlService == null) {
            ynlService = new YnlService();
        }
        return ynlService;
    }

    public void initYnl(){
        loginHelper=YnlAccountHelper.getInstance();
    }

    public void YnlService(String identifier, String password, YnlPwdLoginListener listener) {
        loginHelper.ynlLogin(identifier, password, listener);
    }

    public void initAccountLoginService(Context context,
                                        EditText txt_username,
                                        EditText txt_password,
                                        Button btn_login) {
        ynlAccountLoginService = new YnlAccountLoginService(context, txt_username, txt_password, btn_login);
    }

}
