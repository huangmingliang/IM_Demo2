package com.tencent.qcloud.tlslibrary.http;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by huangmingliang on 2017/4/27.
 */

public class YnlAccountHelper {
    private String TAG="YnlAccountHelper";
    private static YnlAccountHelper ynlAccountHelper;
    private String identifier;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private final String IM="ImConfig";
    private final String ID="identifier";
    private final String SIGN="userSig";
    private Context context;


    static  public YnlAccountHelper getInstance(){
        synchronized (YnlAccountHelper.class){
            if (ynlAccountHelper==null){
                ynlAccountHelper=new YnlAccountHelper();
            }
        }
        return ynlAccountHelper;
    }

    public void init(Context context){
        this.context=context.getApplicationContext();
        preferences=this.context.getSharedPreferences(IM,Context.MODE_PRIVATE);
        editor=preferences.edit();
    }

    public String getIdentifier(){
        return preferences.getString(ID,"");
    }

    public String getUserSig(){
        return preferences.getString(SIGN,"");
    }

    public void clearYnlUserInfo(){
        editor.clear();
        editor.commit();
    }



    public void ynlLogin(final String identifier, String password,String url,final YnlPwdLoginListener listener){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LoginService loginService=retrofit.create(LoginService.class);
        Call<YnlResult> call=loginService.login(identifier,password);
        call.enqueue(new Callback<YnlResult>() {
            @Override
            public void onResponse(Call<YnlResult> call, Response<YnlResult> response) {
                if (response==null){
                    Log.e(TAG,"hml respond="+response);
                    return;
                }
                Log.e(TAG,"body="+response.body());
                YnlResult result=response.body();
                if (result==null){
                    Log.e(TAG,"hml result="+result);
                    return;
                }
                String msg=result.getMsg();
                if ("ok".equalsIgnoreCase(msg)){
                    editor.putString(ID,identifier);
                    editor.putString(SIGN,result.getData().getImtoken());
                    editor.commit();
                    listener.OnPwdLoginSuccess(result);
                }else {
                    editor.clear();
                    listener.OnPwdLoginFail(result.getError());
                }
            }

            @Override
            public void onFailure(Call<YnlResult> call, Throwable t) {
                editor.clear();
                listener.OnPwdLoginFail(t.getMessage());
            }
        });
    }

    interface LoginService{
        @FormUrlEncoded
        @POST("im_login")
        Call<YnlResult> login(@Field("mobile") String mobile, @Field("pwd") String pwd);
    }
}
