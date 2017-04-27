package com.tencent.qcloud.tlslibrary.http;

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
    static  public YnlAccountHelper getInstance(){
        synchronized (YnlAccountHelper.class){
            if (ynlAccountHelper==null){
                ynlAccountHelper=new YnlAccountHelper();
            }
        }
        return ynlAccountHelper;
    }

    public void ynlLogin(String identifier, String password, final YnlPwdLoginListener listener){
        String baseUrl="http://192.168.4.42:81/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LoginService loginService=retrofit.create(LoginService.class);
        Call<YnlResult> call=loginService.login(identifier,password);
        call.enqueue(new Callback<YnlResult>() {
            @Override
            public void onResponse(Call<YnlResult> call, Response<YnlResult> response) {
                listener.OnPwdLoginSuccess(response.body());
            }

            @Override
            public void onFailure(Call<YnlResult> call, Throwable t) {
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
