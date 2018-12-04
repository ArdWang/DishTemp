package com.dt.utils;

public class BroadCast {

    public static final String EXTRA_CHARACTERISTIC_ERROR_MESSAGE = "com.dt.EXTRA_CHARACTERISTIC_ERROR_MESSAGE";
    public static final String ACTION_GATT_CHARACTERISTIC_ERROR = "com.dt.action.ACTION_GATT_CHARACTERISTIC_ERROR";
    public final static String ACTION_GATT_CONNECTED
            = "com.dt.action.ACTION_GATT_CONNECTED";   //连接
    public final static String ACTION_GATT_DISCONNECTED
            = "com.dt.action.ACTION_GATT_DISCONNECTED";  //断开链接
    public final static String ACTION_GATT_SERVICES_DISCOVERED
            = "com.dt.action.ACTION_GATT_SERVICES_DISCOVERED"; //发现设备
    public final static String ACTION_DATA_AVAILABLE
            = "com.dt.action.ACTION_DATA_AVAILABLE";
    //温度数据的传输
    public final static String EXTRA_TEMP_DATA = "com.bw.bwk.action.EXTRA_TEMP_DATA";

    //数据接受成功
    public final static String ACTION_DATA_RECIVER_AVAILABLE = "com.dt.action.ACTION_DATA_RECIVER_AVAILABLE";

}




