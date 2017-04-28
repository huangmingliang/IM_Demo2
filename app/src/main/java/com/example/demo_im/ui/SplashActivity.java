package com.example.demo_im.ui;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.huawei.android.pushagent.PushManager;
import com.tencent.TIMCallBack;
import com.tencent.TIMLogLevel;
import com.tencent.TIMManager;
import com.tencent.TIMOfflinePushToken;
import com.tencent.qcloud.presentation.business.InitBusiness;
import com.tencent.qcloud.presentation.business.LoginBusiness;
import com.tencent.qcloud.presentation.event.FriendshipEvent;
import com.tencent.qcloud.presentation.event.GroupEvent;
import com.tencent.qcloud.presentation.event.MessageEvent;
import com.tencent.qcloud.presentation.event.RefreshEvent;
import com.tencent.qcloud.presentation.presenter.SplashPresenter;
import com.tencent.qcloud.presentation.viewfeatures.SplashView;
import com.example.demo_im.R;
import com.example.demo_im.model.UserInfo;
import com.example.demo_im.utils.PushUtil;
import com.tencent.qcloud.tlslibrary.activity.HostLoginActivity;
import com.tencent.qcloud.tlslibrary.activity.IndependentLoginActivity;
import com.tencent.qcloud.tlslibrary.http.YnlService;
import com.tencent.qcloud.tlslibrary.service.TLSService;
import com.tencent.qcloud.tlslibrary.service.TlsBusiness;
import com.tencent.qcloud.ui.NotifyDialog;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.ArrayList;
import java.util.List;

public class SplashActivity extends FragmentActivity implements SplashView,TIMCallBack {

    SplashPresenter presenter;
    private int LOGIN_RESULT_CODE = 100;
    private int GOOGLE_PLAY_RESULT_CODE = 200;
    private final int REQUEST_PHONE_PERMISSIONS = 0;
    private static final String TAG = SplashActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        clearNotification();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        final List<String> permissionsList = new ArrayList<>();
        /*if (ConnectionResult.SUCCESS != GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)){
            Toast.makeText(this, getString(R.string.google_service_not_available), Toast.LENGTH_SHORT).show();
//            GoogleApiAvailability.getInstance().getErrorDialog(this, GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this),
//                    GOOGLE_PLAY_RESULT_CODE).show();
        }*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if ((checkSelfPermission(Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.READ_PHONE_STATE);
            if ((checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)) permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionsList.size() == 0){
                init();
            }else{
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_PHONE_PERMISSIONS);
            }
        }else{
            init();
        }

    }

    /**
     * 跳转到主界面
     */
    @Override
    public void navToHome() {
        //登录之前要初始化群和好友关系链缓存
        FriendshipEvent.getInstance().init();
        GroupEvent.getInstance().init();
//        String id="17688766150";
//        String userSig="eJxNzVFPgzAUBeD-0tcZLS0tYOLDMkSXiIE5lrmYNATa7moGDSuTsfjfRdyir*e759wTWj69XOdFUbeVFfZoJLpFGF2NMZSysqBANkPoeNz3Pc4dduHcGChFbgVtyn*tffkhRvopuRhjEjAWnFF2BhopcmV-RxljZDg560E2e6irAQh2mEMoxn9oYSfHyYBi4lHGLv9AD3F8n83m6axrVquILI5bq7q43-Cpr1UexpEpQq1Zlq37SG1lD1WbzvWr*3nwMt4tTZVCVvNF0LaQhOQZNu*0nDwGyeRhSpO3m-XOvUNf31HfWN4_";
        String id=UserInfo.getInstance().getId();
        String userSig=UserInfo.getInstance().getUserSig();
        Log.d(TAG,"hml id="+id+" userSig="+userSig);
        LoginBusiness.loginIm(id, userSig, this);
    }

    /**
     * 跳转到登录界面
     */
    @Override
    public void navToLogin() {
//        Intent intent = new Intent(getApplicationContext(), HostLoginActivity.class);
//        startActivityForResult(intent, LOGIN_RESULT_CODE);
        Intent intent = new Intent(SplashActivity.this, IndependentLoginActivity.class);
        startActivityForResult(intent, LOGIN_RESULT_CODE);
    }

    /**
     * 是否已有用户登录
     */
    @Override
    public boolean isUserLogin() {
       //return false;
        return !TextUtils.isEmpty(YnlService.getInstance().getIdentifier());
    }

    /**
     * imsdk登录失败后回调
     */
    @Override
    public void onError(int i, String s) {
        Log.e(TAG, "login error : code " + i + " " + s);
        switch (i) {
            case 6208:
                //离线状态下被其他终端踢下线
                NotifyDialog dialog = new NotifyDialog();
                dialog.show(getString(R.string.kick_logout), getSupportFragmentManager(), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        navToHome();
                    }
                });
                break;
            case 6200:
                Toast.makeText(this,getString(R.string.login_error_timeout),Toast.LENGTH_SHORT).show();
                navToLogin();
                break;
            default:
                Toast.makeText(this,getString(R.string.login_error),Toast.LENGTH_SHORT).show();
                navToLogin();
                break;
        }

    }

    /**
     * imsdk登录成功后回调
     */
    @Override
    public void onSuccess() {

        //初始化程序后台后消息推送
        PushUtil.getInstance();
        //初始化消息监听
        MessageEvent.getInstance();
        String deviceMan = Build.MANUFACTURER;
        //注册小米和华为推送
        if (deviceMan.equals("Xiaomi") && shouldMiInit()){
            MiPushClient.registerPush(getApplicationContext(), "2882303761517480335", "5411748055335");
        }else if (deviceMan.equals("HUAWEI")){
            PushManager.requestToken(this);
        }
        LoggerInterface newLogger = new LoggerInterface() {
            @Override
            public void setTag(String tag) {
                // ignore
            }
            @Override
            public void log(String content, Throwable t) {
                Log.d(TAG, content, t);
            }
            @Override
            public void log(String content) {
                Log.d(TAG, content);
            }
        };
        Logger.setLogger(this, newLogger);

       /* String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "refreshed token: " + refreshedToken);

        if(!TextUtils.isEmpty(refreshedToken)) {
            TIMOfflinePushToken param = new TIMOfflinePushToken();
            param.setToken(refreshedToken);
            param.setBussid(169);
            TIMManager.getInstance().setOfflinePushToken(param);
        }*/
//        MiPushClient.clearNotification(getApplicationContext());
        Log.d(TAG, "hml imsdk env " + TIMManager.getInstance().getEnv());
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "hml onActivityResult code:" + requestCode);
        if (LOGIN_RESULT_CODE == requestCode) {
         Log.d(TAG, "hml login error no " + YnlService.getInstance().getLastErrno());
            if (0 == YnlService.getInstance().getLastErrno()){
                UserInfo.getInstance().setId(YnlService.getInstance().getIdentifier());
                UserInfo.getInstance().setUserSig(YnlService.getInstance().getUserSig());
                navToHome();
            } else if (resultCode == RESULT_CANCELED){
                finish();
            }
          //navToHome();

        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PHONE_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    init();
                } else {
                    Toast.makeText(this, getString(R.string.need_permission),Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private void init(){
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        int loglvl = pref.getInt("loglvl", TIMLogLevel.DEBUG.ordinal());
        //初始化IMSDK
        InitBusiness.start(getApplicationContext(),loglvl);
        //初始化TLS
        TlsBusiness.init(getApplicationContext());
        //设置刷新监听
        RefreshEvent.getInstance();
        Log.d(TAG,"hml id="+YnlService.getInstance().getIdentifier());
        UserInfo.getInstance().setId(YnlService.getInstance().getIdentifier());
        UserInfo.getInstance().setUserSig(YnlService.getInstance().getUserSig());
        presenter = new SplashPresenter(this);
        presenter.start();
    }

    /**
     * 判断小米推送是否已经初始化
     */
    private boolean shouldMiInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = android.os.Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 清楚所有通知栏通知
     */
    private void clearNotification(){
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        MiPushClient.clearNotification(getApplicationContext());
    }
}
