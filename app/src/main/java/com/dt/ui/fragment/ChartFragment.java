package com.dt.ui.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.dt.R;
import com.dt.base.ui.fragment.BaseFragment;
import com.dt.db.TmpBean;
import com.dt.ui.activity.DataActivity;
import com.dt.ui.manager.ChartManager;
import com.dt.ui.model.ChartModel;
import com.dt.utils.BroadCast;
import com.dt.utils.DateUtil;
import com.dt.utils.NormalUtil;
import com.github.mikephil.charting.charts.LineChart;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ChartFragment extends BaseFragment {

    private final static String TAG = "ChartFragment";

    private DataActivity dataActivity;

    private TextView mMacTv,mMaxTv,mUnitTv;

    private LinearLayout mMoreData;

    private LineChart mTmpChart;

    private ChartManager chartManager;

    private List<TmpBean> tmpList = new ArrayList<>();

    //默认为0
    private int mCount=0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_chart,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view) {
        mMacTv = view.findViewById(R.id.mMacTv);
        mMaxTv = view.findViewById(R.id.mMaxTv);
        mUnitTv = view.findViewById(R.id.mUnitTv);
        mMoreData = view.findViewById(R.id.mMoreData);
        mTmpChart = view.findViewById(R.id.mTmpChart);
    }

    private void initData(){
        dataActivity = (DataActivity)getActivity();
    }

    @Override
    public void onResume() {
        super.onResume();
        dataActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        //获取当前的数据
        initTmpData();
    }

    /**
     * 本次更新增加了 设备的存储地址的利用来解决存储多个设备的的数据保存问题
     * 手机的api必须在19以上不然会报错
     * 创建接受 蓝牙的广播
     */
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Bundle extras = intent.getExtras();
            if (BroadCast.ACTION_DATA_AVAILABLE.equals(action)) {
                if(extras!=null) {
                    // 如果是蓝牙的可读特征显示
                    if (extras.containsKey(BroadCast.EXTRA_TEMP_DATA)) {
                        String tempdata = intent.getStringExtra(BroadCast.EXTRA_TEMP_DATA);
                        displayTmpData(tempdata);
                    }
                }
            }else if(BroadCast.ACTION_DATA_RECIVER_AVAILABLE.equals(action)){
                Log.i(TAG,"ReciveData Success");
            }
        }
    };


    private void initTmpData(){
        tmpList = ChartModel.findTmpDataList(dataActivity.mac);
        String unit = dataActivity.normalShared.getString("unit"+dataActivity.mac);
        mMacTv.setText(dataActivity.mac);
        mUnitTv.setText(unit);

        if(tmpList.size()>0) {
            setLoadTempData(unit);
            ChartModel.listMax(tmpList,mMaxTv,unit);
        }
    }

    /**
     * 接受广播来处理温度数据
     */
    private void displayTmpData(String data){
        String unit;
        if(data!=null){
            String []allData = data.split(",");
            if(mCount<=100){
                tmpList = ChartModel.getCurrentData(tmpList,allData[0],allData[1]);

                if(allData[2].equals("F")){
                    unit = "°F";
                    mUnitTv.setText("°F");
                }else{
                    unit = "°C";
                    mUnitTv.setText("°C");
                }
                setLoadTempData(unit);
                ChartModel.listMax(tmpList,mMaxTv,unit);
                mCount++;
            }else{
                mCount=0;
                if(tmpList.size()>0){
                    tmpList.clear();
                }
                initTmpData();
            }
        }
    }


    /**
     * 加载温度数据
     */
    private void setLoadTempData(String unit) {
        if(chartManager==null){
            chartManager = new ChartManager(getContext(),mTmpChart,unit);
        }

        // 添加X轴坐标
        List<String> xVals = new ArrayList<>();
        for (int i = 0; i < tmpList.size(); i++) {
            TmpBean temp = tmpList.get(i);
            Long a = temp.getTime();
            String ab = DateUtil.getLongToString(a,"HH:mm:ss");
            xVals.add(ab);
        }

        // 添加Y轴坐标
        List<Float> yVals = new ArrayList<>();
        for (int i = 0; i < tmpList.size(); i++) {
            TmpBean temp = tmpList.get(i);
            String y = temp.getTmp();
            if(unit.equals("°F")){
                if(y!=null) {
                    yVals.add(Float.parseFloat(y));
                }
            }else{
                float valf = Float.parseFloat(y);
                float val = NormalUtil.ConvertCelciusToFahren(valf);
                String tempFvalue =val+"";
                String value = NormalUtil.saveOneDecimalPoint(tempFvalue);
                if(value!=null) {
                    yVals.add(Float.parseFloat(value));
                }
            }
        }

        if(tmpList.size()>0) {
            chartManager.showLineChart(xVals, yVals, getResources().getColor(R.color.colorNav),
                    "Diagram (degree/time)"+" "+getCurrentDataTime());
        }

        chartManager.setDescription("");
    }

    /**
     * 转换时间格式
     * @return
     */
    private String getCurrentDataTime(){
        Date date = new Date(System.currentTimeMillis());
        String times =  DateUtil.getDateToString(date,"yyyy-MM-dd");
        String ss[] = times.split(" ");
        String sss[] = ss[0].split("-");
        return sss[1]+"/"+sss[2]+"/"+sss[0];
    }


    /**
     * 发送服务广播
     * @return intentFilter
     */
    private IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BroadCast.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BroadCast.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BroadCast.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BroadCast.ACTION_DATA_AVAILABLE);
        //接受数据成功的广播
        intentFilter.addAction(BroadCast.ACTION_DATA_RECIVER_AVAILABLE);
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        return intentFilter;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataActivity.unregisterReceiver(mGattUpdateReceiver);
    }


}
