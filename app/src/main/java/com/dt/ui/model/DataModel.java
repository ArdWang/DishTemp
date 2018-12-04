package com.dt.ui.model;

import com.dt.db.TmpBean;
import com.dt.utils.DateUtil;
import com.dt.utils.NormalShared;

import java.util.Date;

public class DataModel {

    /**
     * 存储温度数据到数据库中
     */
    public static void saveTmpData(String data,NormalShared normalShared){
        String unit;
        String []allData = data.split(",");
        String mac = allData[4];
        String []macs = mac.split(":");
        String allmac = "S/N:0"+macs[0]+macs[1]+macs[2]+macs[3]+macs[4]+macs[5];

        allmac = allmac.toUpperCase();

        //计算时间
        Long time = DateUtil.getDateToLong(new Date(System.currentTimeMillis()));

        TmpBean tp = new TmpBean();
        tp.setTmp(allData[0]);
        tp.setMac(allmac);
        tp.setMax(allData[1]);
        if(allData[2].equals("F")) {
            tp.setUnit("°F");
            unit = "°F";
        }else{
            tp.setUnit("°C");
            unit = "°C";
        }

        tp.setBat(allData[3]);
        tp.setTime(time);
        tp.save();

        //存储单位值
        normalShared.saveString("unit"+allmac,unit);
    }
}
