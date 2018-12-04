package com.dt.ui.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.dt.R;
import com.dt.base.ui.activity.BaseActivity;
import com.dt.service.BluetoothService;
import com.dt.ui.adapter.DataVpAdapter;
import com.dt.ui.model.DataModel;
import com.dt.utils.BroadCast;
import com.dt.utils.GattAttributes;
import com.dt.utils.HandlerUtils;
import com.dt.utils.NormalShared;
import com.dt.widgets.ProgressLoading;
import java.util.ArrayList;
import java.util.List;


/**
 * 用于连接蓝牙的时候使用
 * 共用一个连接蓝牙
*/
public class DataActivity extends BaseActivity implements View.OnClickListener,HandlerUtils.OnReceiveMessageListener{
    private List<String> titles = new ArrayList<>();
    private DataVpAdapter dataVpAdapter;
    private ViewPager mDataVp;
    private TabLayout mDataTab;
    private ImageView mLeftIv;
    //蓝牙服务
    public BluetoothService bluetoothService;

    private String deviceName;

    private String deviceMac;

    private static final String TAG = "DeviceActivity";

    //温度
    public BluetoothGattCharacteristic tempCharacteristic;

    //开关按钮
    public BluetoothGattCharacteristic closeCharacteristic;

    private LinearLayout mRightLl;

    private HandlerUtils.HandlerHolder handlerHolder;

    //执行一次
    private boolean onceHandler;

    private ProgressLoading progressLoading;

    public String mac;

    public NormalShared normalShared;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        initView();
        initData();
    }

    private void initView() {
        mDataVp = findViewById(R.id.mDataVp);
        mDataTab = findViewById(R.id.mDataTab);
        mLeftIv = findViewById(R.id.mLeftIv);
        mRightLl = findViewById(R.id.mRightLl);

        mLeftIv.setOnClickListener(this);
        mRightLl.setOnClickListener(this);


    }

    private void initData(){
        normalShared = new NormalShared();
        deviceName = getIntent().getStringExtra("deviceName");
        deviceMac = getIntent().getStringExtra("deviceMac");

        if(deviceMac!=null){
            String []macs = deviceMac.split(":");
            String allmac = "S/N:"+macs[0]+macs[1]+macs[2]+macs[3]+macs[4]+macs[5];
            mac = allmac.toUpperCase();
        }

        titles.add("Temp");
        titles.add("Chart");

        dataVpAdapter = new DataVpAdapter(getSupportFragmentManager(),titles);
        mDataVp.setAdapter(dataVpAdapter);
        mDataTab.setupWithViewPager(mDataVp);

        onceHandler = true;
        handlerHolder = new HandlerUtils.HandlerHolder(this);
        handlerHolder.post(runnable);

        Intent gattServiceIntent = new Intent(this, BluetoothService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        if(progressLoading==null) {
            progressLoading = ProgressLoading.create(this);
            progressLoading.showLoading();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(tempCharacteristic!=null&&onceHandler){
                prepareBroadcastDataNotify(tempCharacteristic);
                handlerHolder.removeCallbacks(runnable);
                onceHandler = false;
            }
            handlerHolder.postDelayed(runnable,2000);
        }
    };


    /**
     * 启动服务
     */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
    }


    /**
     * 蓝牙连接
     */
    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            //断开的时候作出来判断
        }
        @Override
        public void onServiceConnected(ComponentName componentName,
                                       IBinder service) {
            bluetoothService = ((BluetoothService.LocalBinder) service)
                    .getService();
            if (!bluetoothService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }

            if(deviceMac!=null) {
                bluetoothService.connect(deviceMac, DataActivity.this);
            }
        }
    };

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
        intentFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        intentFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        return intentFilter;
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
            // 如果已经连接
            if (BroadCast.ACTION_GATT_CONNECTED.equals(action)) {
                /**
                 * 如果连接成功就要存储设备信息
                 */
            } else if (BroadCast.ACTION_GATT_DISCONNECTED.equals(action)) {
                // 如果没有连接
                /**
                 * 当蓝牙连接出现断开的时候那么需要把该界面finish()掉
                 * 我这里是清除了所有的界面 除了MainActivity
                 */
                finish();
            } else if (BroadCast.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // 发现服务
                displayGattServices(bluetoothService.getSupportedGattServices());
                //如果是5.1版本以上需要发送mtu
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    bluetoothService.exchangeGattMtu(512);
                }
            }

            else if (BroadCast.ACTION_DATA_AVAILABLE.equals(action)) {
                if(extras!=null) {
                    // 如果是蓝牙的可读特征显示
                    if (extras.containsKey(BroadCast.EXTRA_TEMP_DATA)) {
                        String tempdata = intent.getStringExtra(BroadCast.EXTRA_TEMP_DATA);
                        //温度处理
                        displayTempData(tempdata);
                    }
                }
            }
        }
    };


    private void displayTempData(String data){
        if(progressLoading!=null){
            progressLoading.hideLoading();
        }

        if(data!=null){
            //存储温度数据到数据库
            DataModel.saveTmpData(data,normalShared);
        }
    }


    /**
     * 查找蓝牙设备中的服务 信息 发现有没有可以使用的服务
     * @param gattServices
     */
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) {
            return;
        }
        for (BluetoothGattService gattService : gattServices) {
            List<BluetoothGattCharacteristic> gattCharacteristicss = gattService.getCharacteristics();
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristicss) {
                String uuidchara = gattCharacteristic.getUuid().toString();
                Log.i("===特征===",uuidchara);
                if (uuidchara.equalsIgnoreCase(GattAttributes.DT_PROJECT_TEMP)) {
                    tempCharacteristic = gattCharacteristic;
                }else if(uuidchara.equalsIgnoreCase(GattAttributes.DT_PROJECT_SWITCH)){
                    closeCharacteristic = gattCharacteristic;
                }
            }
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     * 准备广播接收机广播通知特性
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataIndicate(
            BluetoothGattCharacteristic gattCharacteristic) {
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_INDICATE) > 0) {
            Log.i(TAG,"执行发现通知操作!");
            bluetoothService.setCharacteristicIndication(gattCharacteristic,
                    true);
        }
    }

    /**
     * Preparing Broadcast receiver to broadcast notify characteristics
     *
     * @param gattCharacteristic
     */
    void prepareBroadcastDataNotify(
            BluetoothGattCharacteristic gattCharacteristic) {
        if ((gattCharacteristic.getProperties() | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
            try {
                bluetoothService.setCharacteristicNotification(gattCharacteristic,
                        true);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.mLeftIv:
                finish();
                break;
            case R.id.mRightLl:
                prepareBroadcastDataNotify(tempCharacteristic);
                break;
        }
    }

    @Override
    public void handlerMessage(Message msg) {

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(mGattUpdateReceiver);

        //断开蓝牙服务连接
        if(bluetoothService!=null){
            bluetoothService.disconnect();
        }

        unbindService(mServiceConnection);

        if(handlerHolder!=null){
            handlerHolder.removeCallbacksAndMessages(null);
        }

        if(progressLoading!=null){
            progressLoading.hideLoading();
        }
    }
}
