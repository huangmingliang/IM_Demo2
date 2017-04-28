package com.tencent.qcloud.tlslibrary.http;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.tencent.qcloud.tlslibrary.helper.Util;
import com.tencent.qcloud.tlslibrary.service.Constants;

/**
 * Created by huangmingliang on 2017/4/27.
 */

public class YnlAccountLoginService {

    private String TAG="YnlAccountLoginService";
    private Context context;
    private EditText txt_username;
    private EditText txt_password;
    private EditText txt_url;
    private YnlService ynlService;

    private String username;
    private String password;
    private String url;

    public  static PwdLoginListener pwdLoginListener;
    public YnlAccountLoginService(Context context,
                                  EditText txt_username,
                                  EditText txt_password,
                                  EditText txt_url,
                                  Button btn_login) {
        this.context = context;
        this.txt_username = txt_username;
        this.txt_password = txt_password;
        this.txt_url=txt_url;

        ynlService = YnlService.getInstance();

        pwdLoginListener = new PwdLoginListener();

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = YnlAccountLoginService.this.txt_username.getText().toString();
                password = YnlAccountLoginService.this.txt_password.getText().toString();
                url=YnlAccountLoginService.this.txt_url.getText().toString();
                // 验证用户名和密码的有效性
                if (username.length() == 0 || password.length() == 0||url.length()==0) {
                    Util.showToast(YnlAccountLoginService.this.context, "用户名密码地址不能为空");
                    return;
                }

                ynlService.PwdLogin(username, password,url, pwdLoginListener);
            }
        });
    }
    class PwdLoginListener implements YnlPwdLoginListener{

        @Override
        public void OnPwdLoginSuccess(YnlResult var1) {
            Util.showToast(context, "登录成功");
            //TLSService.getInstance().setLastErrno(0);
            YnlService.getInstance().setLastErrno(0);
            jumpToSuccActivity();
        }

        @Override
        public void OnPwdLoginFail(String var1) {
            Util.showToast(context, "登录失败");
            Log.e(TAG,"error:"+var1);
        }
    }

    void jumpToSuccActivity() {
        Log.d(TAG, "jumpToSuccActivity");
        String thirdappPackageNameSucc = Constants.thirdappPackageNameSucc;
        String thirdappClassNameSucc = Constants.thirdappClassNameSucc;

        Intent intent = new Intent();
        intent.putExtra(Constants.EXTRA_LOGIN_WAY, Constants.USRPWD_LOGIN);
        intent.putExtra(Constants.EXTRA_USRPWD_LOGIN, Constants.USRPWD_LOGIN_SUCCESS);
        if (thirdappPackageNameSucc != null && thirdappClassNameSucc != null) {
            intent.setClassName(thirdappPackageNameSucc, thirdappClassNameSucc);
            context.startActivity(intent);
        } else {
            Log.d(TAG, "finish current activity");
            ((Activity) context).setResult(Activity.RESULT_OK, intent);
            ((Activity) context).finish();
        }
    }

}
