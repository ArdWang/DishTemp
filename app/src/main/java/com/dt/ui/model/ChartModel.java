package com.dt.ui.model;

import android.annotation.SuppressLint;
import android.widget.TextView;

import com.dt.db.TmpBean;
import com.dt.utils.DateUtil;
import com.dt.utils.NormalUtil;

import org.litepal.LitePal;
import java.util.Date;
import java.util.List;

public class ChartModel {
    /**
     * 获取当前的时间的100个数据
     * @param mac
     * @return tmpList
     */
    public static List<TmpBean> findTmpDataList(String mac){
        List<TmpBean> tmpList = LitePal.where("mac like ?",mac).order("time").limit(100).find(TmpBean.class);
        return tmpList;
    }


    /**
     * 获取当前的温度数据
     * @param list
     * @return list
     */
    public static List<TmpBean> getCurrentData(List<TmpBean> list,String tmp,String max){
        Date date = new Date(System.currentTimeMillis());
        Long time = DateUtil.getDateToLong(date);
        TmpBean temp = new TmpBean();
        temp.setTmp(tmp);
        temp.setMax(max);
        temp.setTime(time);
        list.add(temp);
        return list;
    }


    @SuppressLint("SetTextI18n")
    public static void listMax(List<TmpBean> list, TextView tv, String unit){
        float max=0;
        for(int i=0;i<list.size();i++){
            TmpBean tp = list.get(i);
            float tmax = Float.parseFloat(tp.getMax());
            if(tmax<999&&tmax>-999){
                if(unit.equals("°C")){
                    float aa = NormalUtil.ConvertCelciusToFahren(tmax);
                    if(i==0){
                        max = aa;
                    }
                    if(aa>max){
                        max = aa;
                    }
                }else{
                    if(i==0){
                        max = tmax;
                    }
                    if(tmax>max){
                        max = tmax;
                    }
                }
                tv.setText("Max: "+tmax+unit);
            }

            if(tmax == 999){
                tv.setText("Max: "+"HHH");
            }else if(tmax == -999){
                tv.setText("Max: "+"LLL");
            }else if(tmax == 1111){
               tv.setText("Max: "+"---");
            }
        }
    }

}
