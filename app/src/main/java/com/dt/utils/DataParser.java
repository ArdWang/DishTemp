package com.dt.utils;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;

/**
    温度数据解析
 * */
public class DataParser {
    //温度数据处理
    public static String getTempData(BluetoothGattCharacteristic characteristic){
        byte[] data = characteristic.getValue();
        String qq = byteArrayToStr(data);
        Log.i("qq is ",qq);
        return qq;
    }

    //数组转为string
    private static String byteArrayToStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = new String(byteArray);
        return str;
    }
}
