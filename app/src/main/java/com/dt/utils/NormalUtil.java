package com.dt.utils;

/**
 * Created by rnd on 2017/10/17.
 */

public class NormalUtil {

    /**
     * 十进制转为十六进制
     * @param one
     * @param two
     * @param three
     * @param four
     * @param five
     * @param six
     * @return
     */
    public static String TentoHex(int one, int two, int three, int four, int five, int six){
        String one16 = Integer.toHexString(one);
        String two16 = Integer.toHexString(two);
        String three16 = Integer.toHexString(three);
        String four16 = Integer.toHexString(four);
        String five16 = Integer.toHexString(five);
        String six16 = Integer.toHexString(six);
        String all16 = one16+two16+three16+four16+five16+six16;
        return all16;
    }


    // Converts to celcius
    // 转换为摄氏度
    public static float convertFahrenheitToCelcius(float fahrenheit) {
        return ((fahrenheit - 32) * 5 / 9);
    }

    //Converts to FSDU
    public static float ConvertCelciusToFahren(float celcius){
        return  ((celcius * 9 / 5) + 32);
    }

    /**
     * 保留1位小数
     */
    public static String saveOneDecimalPoint(String name){
        String b = null;
        if(name != null){
            double a =Double.parseDouble(name);
            String c = String.format("%.1f",a); //保留1位小数
            b = c;
        }
        return b;
    }

    // 转换为摄氏度
    public static float convertFahrenheitToCelciusCal(float fahrenheit) {
        return (float) (fahrenheit/1.8);
    }

    //Converts to FSDU
    public static float ConvertCelciusToFahrenCal(float celcius){
        return (float) (celcius*1.8);
    }

}
