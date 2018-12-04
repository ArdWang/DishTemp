package com.dt.base;


import android.content.Context;

import org.litepal.LitePalApplication;


/**
 * Created by rnd on 2018/4/8.
 *
 */

public class BaseApplication extends LitePalApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    //获取全局变量的Context
    public static Context getContext(){
        return context;
    }

}
