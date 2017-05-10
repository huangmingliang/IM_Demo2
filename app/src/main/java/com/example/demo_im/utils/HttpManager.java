package com.example.demo_im.utils;


import com.example.demo_im.model.SearchResult;


import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by huangmingliang on 2017/5/9.
 */

public class HttpManager {
    private String baseUrl="http://192.168.4.41:81";
    private RequestService requestService;
    private static HttpManager httpManager;

   public HttpManager(){
       init();
   }
    public static HttpManager getInstance(){
        synchronized (HttpManager.class){
            if (httpManager ==null){
                httpManager =new HttpManager();
            }
        }
        return httpManager;
    }

    private void init(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        requestService=retrofit.create(RequestService.class);
    }

    public void searchUser(String name, Observer<SearchResult> observer){
        requestService.searchMobileByUser(name)
                      .subscribeOn(Schedulers.io())
                      .unsubscribeOn(Schedulers.io())
                      .observeOn(AndroidSchedulers.mainThread())
                      .subscribe(observer);
    }

    private interface RequestService{
        @FormUrlEncoded
        @GET("/im_search/{key}")
        Observable<SearchResult> searchMobileByUser(@Path("key")String key);
    }
}
