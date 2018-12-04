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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.dt.R;
import com.dt.base.ui.fragment.BaseFragment;
import com.dt.ext.CommonExt;
import com.dt.ui.activity.DataActivity;
import com.dt.utils.BroadCast;


public class TempFragment extends BaseFragment {

    private final static String TAG = "TempFragment";

    private DataActivity dataActivity;

    //显示TextView
    private TextView mMacTv,mMaxTv,mMaxUnitTv,mTmpTv,mTmpUnitTv;
    //显示开关
    private ImageView mSwitch;

    private boolean mswitch;

    private String mac;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_temp,container,false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        initData();
    }

    private void initView(View view){
        mMacTv = view.findViewById(R.id.mMacTv);
        mMaxTv = view.findViewById(R.id.mMaxTv);
        mTmpTv = view.findViewById(R.id.mTmpTv);
        mTmpUnitTv = view.findViewById(R.id.mTmpUnitTv);
        mMaxUnitTv = view.findViewById(R.id.mMaxUnitTv);

        mSwitch = view.findViewById(R.id.mSwitch);

        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mswitch = dataActivity.normalShared.getBool(mac+"switch");
                if(mswitch){
                    mSwitch.setImageResource(R.drawable.switchoff);
                    boolean a = dataActivity.bluetoothService.writeCharacteristic(dataActivity.closeCharacteristic,"PD");
                    if(a){
                        dataActivity.normalShared.saveBool(mac+"switch",false);
                        CommonExt.toast("关闭成功!");
                    }
                }else{
                    mSwitch.setImageResource(R.drawable.switchon);
                    boolean a = dataActivity.bluetoothService.writeCharacteristic(dataActivity.closeCharacteristic,"PO");
                    if(a){
                        dataActivity.normalShared.saveBool(mac+"switch",true);
                        CommonExt.toast("打开成功!");
                    }
                }
            }
        });
    }

    private void initData(){
        dataActivity = (DataActivity)getActivity();
        //得到布尔值
        mswitch = dataActivity.normalShared.getBool(mac+"switch");

        if(mswitch){
            mSwitch.setImageResource(R.drawable.switchon);
        }else{
            mSwitch.setImageResource(R.drawable.switchoff);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        dataActivity.registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
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
                        //温度处理
                        displayTempData(tempdata);
                    }
                }
            }else if(BroadCast.ACTION_DATA_RECIVER_AVAILABLE.equals(action)){
                Log.i(TAG,"ReciveData Success");
            }
        }
    };


    private void displayTempData(String data){
        if(data!=null){
            String []allData = data.split(",");
            String mac = allData[4];
            String []macs = mac.split(":");
            String allmac = "S/N:0"+macs[0]+macs[1]+macs[2]+macs[3]+macs[4]+macs[5];
            allmac = allmac.toUpperCase();
            mMacTv.setText(allmac);

            mMaxTv.setText(allData[1]);

            mTmpTv.setText(allData[0]);

            if(allData[2].equals("F")) {
                mMaxUnitTv.setText("°F");
                mTmpUnitTv.setText("°F");
            }else{
                mMaxUnitTv.setText("°C");
                mTmpUnitTv.setText("°C");
            }
        }
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
