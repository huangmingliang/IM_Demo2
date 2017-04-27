package com.example.demo_im.http;

import com.tencent.qcloud.tlslibrary.service.TLSService;

import tencent.tls.platform.TLSLoginHelper;
import tencent.tls.platform.TLSPwdLoginListener;

/**
 * Created by huangmingliang on 2017/4/27.
 */

public class YnlService {

    private static YnlService ynlService;
    private YnlAccountHelper loginHelper;

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

}
