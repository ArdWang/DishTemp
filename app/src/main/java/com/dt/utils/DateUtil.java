package com.dt.utils;
import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;



@SuppressLint("SimpleDateFormat")
public class DateUtil {
    private static SimpleDateFormat sf = null;

    /**
     * Get current time
     * df is the parameter of the time format to be passed in
     * @param df
     * @return
     */

    public static String getCurrentDate(String df){
        Date d = new Date();
        sf = new SimpleDateFormat(df);
        return sf.format(d);
    }

    /**
     * longtime to string
     * @param time
     * @param df
     * @return
     */

    public static String getLongToString(Long time, String df){
        Date d = new Date(time);
        sf = new SimpleDateFormat(df);
        return sf.format(d);
    }


    /**
     * datetime to String
     * @param date
     * @param df
     * @return
     */

    public static String getDateToString(Date date, String df){
        sf = new SimpleDateFormat(df);
        return sf.format(date);
    }


    /**
     * Date to Long
     * @param date
     * @return
     */
    public static Long getDateToLong(Date date){
        return date.getTime();
    }


    /**
     * String to Long
     * @param time
     * @param df
     * @return
     */
    public static Long getStringToLong(String time, String df){
        sf = new SimpleDateFormat(df);
        Date d = new Date();
        try{
            d = sf.parse(time);
        }catch (Exception e){
            e.printStackTrace();
        }
        return d.getTime();
    }

    /**
     * String to Date
     * @param time
     * @param df
     * @return
     */
    public static Date getStringToDate(String time, String df){
        sf = new SimpleDateFormat(df);
        Date d = new Date();
        try {
            d = sf.parse(time);
        }catch (Exception e){
            e.printStackTrace();
        }
        return d;
    }


    /**
     * 返回当前月份日期位于周几
     * @param year
     *
     * @param month
     *
     * @return
     * 	日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
    public static int getDayWeek(int year, int month,int day){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month-1, day);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

}
