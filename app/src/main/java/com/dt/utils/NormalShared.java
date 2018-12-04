package com.dt.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.dt.base.BaseApplication;

public class NormalShared {
    private SharedPreferences sp = BaseApplication.getContext().getSharedPreferences("sp_dt",Context.MODE_PRIVATE);
    private SharedPreferences.Editor ed;


    public NormalShared(){
        ed = sp.edit();
    }


    /**
     * 存储开关值
     */
    public void saveBool(String key,Boolean value){
        ed.putBoolean(key,value);
        ed.apply();
    }

    /**
     * 得到布尔值 默认是为开
     */
    public Boolean getBool(String key){
        return sp.getBoolean(key,true);
    }

    /**
     * 存储字符类型
     */
    public void saveString(String key,String value){
        ed.putString(key,value);
        ed.apply();
    }

    /**
     * 获取字符类型
     */
    public String getString(String key){
        return sp.getString(key,null);
    }


}
