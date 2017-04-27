package com.example.demo_im.http;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.POST;

/**
 * Created by huangmingliang on 2017/4/27.
 */

public class YnlAccountHelper {
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
        String baseUrl="https://192.168.4.42:81/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        LoginService loginService=retrofit.create(LoginService.class);
        Call<YnUserInfo> call=loginService.login(identifier,password);
        call.enqueue(new Callback<YnUserInfo>() {
            @Override
            public void onResponse(Call<YnUserInfo> call, Response<YnUserInfo> response) {
                listener.OnPwdLoginSuccess(response.body());
            }

            @Override
            public void onFailure(Call<YnUserInfo> call, Throwable t) {
                listener.OnPwdLoginFail(t.getMessage());
            }
        });
    }

    interface LoginService{
        @POST("im_login")
        Call<YnUserInfo> login(@Field("mobile") String mobile,@Field("pwd") String pwd);
    }
}
